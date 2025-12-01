package com.binlah.sap.btp.cpi.cpi_message_viewer.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.binlah.sap.btp.cpi.cpi_message_viewer.cqn.IntegrationRuntimeArtifactsCqnVisitor;
import com.binlah.sap.btp.cpi.cpi_message_viewer.cqn.MessageProcessingLogCqnVisitor;
import com.binlah.sap.cap.vdm.namespaces.integrationcontent.IntegrationRuntimeArtifact;
import com.binlah.sap.cap.vdm.namespaces.integrationcontent.IntegrationRuntimeArtifactFluentHelper;
import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.IntegrationArtifact;
import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageProcessingLog;
import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageProcessingLogByKeyFluentHelper;
import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageProcessingLogCustomHeaderProperty;
import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageProcessingLogFluentHelper;
import com.binlah.sap.cap.vdm.namespaces.messagestore.MessageStoreEntry;
import com.binlah.sap.cap.vdm.services.DefaultIntegrationContentService;
import com.binlah.sap.cap.vdm.services.DefaultMessageProcessingLogsService;
import com.binlah.sap.cap.vdm.services.DefaultMessageStoreService;
import com.sap.cds.ResultBuilder;
import com.sap.cds.ql.cqn.AnalysisResult;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCount;

import cds.gen.remoteservice.IntegrationRuntimeArtifacts_;
import cds.gen.remoteservice.MessageProcessingLogCustomHeaderProperties_;
import cds.gen.remoteservice.MessageProcessingLogs_;
import cds.gen.remoteservice.MessageStoreEntries_;
import cds.gen.remoteservice.RemoteService_;
import jakarta.annotation.PostConstruct;

@Component
@ServiceName(RemoteService_.CDS_NAME)
public class RemoteServiceHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceHandler.class);

    // /Date(1694851200000)/ or /Date(1694851200000+0700)/
    private static final Pattern ODATA_V2_DATE = Pattern.compile("^/Date\\(([-]?\\d+)(?:([+-]\\d{4}))?\\)/$");

    private HttpDestination dest;
    private DefaultMessageProcessingLogsService messageProcessingLogsService;
    private DefaultMessageStoreService messageStoreService;
    private DefaultIntegrationContentService integrationContentService;

    @PostConstruct
    void init() {
        logger.info("MonitoringServiceHandler bound to entity: {}", MessageProcessingLogs_.CDS_NAME);
        this.messageProcessingLogsService = new DefaultMessageProcessingLogsService().withServicePath("");
        this.messageStoreService = new DefaultMessageStoreService().withServicePath("");
        this.integrationContentService = new DefaultIntegrationContentService().withServicePath("");
    }

    private HttpDestination getDestination() {
        logger.info("Starting getDestination");
        if (this.dest == null) {
            this.dest = DestinationAccessor.getDestination("BTP_CloudIntegration_API").asHttp();
        }
        logger.info("this.dest: {}", this.dest);
        return this.dest;
    }

    // --------------------------------------------------------------------
    // ----------------------- MessageProcessingLogs -----------------------
    // --------------------------------------------------------------------

    @On(event = CqnService.EVENT_READ, entity = MessageProcessingLogs_.CDS_NAME)
    public void onReadMessageProcessingLogs(final CdsReadEventContext ctx) {
        logger.info("Starting onReadMessageProcessingLogs");
        logger.info(" with CQN: {}", ctx.getCqn());

        CqnSelect select = ctx.getCqn();

        CdsModel cdsModel = ctx.getModel();
        CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(cdsModel);
        AnalysisResult result = cqnAnalyzer.analyze(select.ref());

        // boolean hasSelectWhere = select.where() != null && !select.where().isEmpty();
        boolean hasTargetKey = !result.targetKeys().isEmpty();
        boolean hasLimit = select.hasLimit();
        boolean hasInlineCount = select.hasInlineCount();

        boolean isObjectPage = hasTargetKey && !hasLimit && !hasInlineCount;
        logger.info("isObjectPage: {}", isObjectPage);

        // Map<String, Object> rootKeys = result.rootKeys();

        if (isObjectPage) {
            Map<String, Object> rootKeys = result.rootKeys();
            String MessageGuid = (String) rootKeys.get("MessageGuid");

            final MessageProcessingLogByKeyFluentHelper fluentHelper = this.messageProcessingLogsService
                    .getMessageProcessingLogByKey(MessageGuid);

            final MessageProcessingLog mpl = fluentHelper.executeRequest(getDestination());

            if (mpl != null) {
                Map<String, Object> row = mapMessageProcessingLogRow(mpl);
                ctx.setResult(Collections.singletonList(row));
            }
        } else {
            // MessageProcessingLogFluentHelper fluentHelper = new
            // MessageProcessingLogFluentHelper(
            // "MessageProcessingLogs", "")
            MessageProcessingLogFluentHelper fluentHelper = this.messageProcessingLogsService
                    .getAllMessageProcessingLog()
                    .select(
                            MessageProcessingLog.MESSAGE_GUID,
                            MessageProcessingLog.CORRELATION_ID,
                            MessageProcessingLog.LOG_START,
                            MessageProcessingLog.LOG_END,
                            MessageProcessingLog.STATUS,
                            MessageProcessingLog.field("IntegrationArtifact",
                                    IntegrationArtifact.class),
                            MessageProcessingLog.CUSTOM_STATUS);

            MessageProcessingLogCqnVisitor visitor = new MessageProcessingLogCqnVisitor(fluentHelper);
            select.accept(visitor);

            final List<MessageProcessingLog> entities = fluentHelper.executeRequest(getDestination());
            final FluentHelperCount fluentHelperCount = fluentHelper.count();
            final long count = fluentHelperCount.executeRequest(getDestination());

            ctx.setResult(
                    ResultBuilder.selectedRows(mapMessageProcessingLogToRows(entities)).inlineCount(count).result());
        }

    }

    private List<Map<String, Object>> mapMessageProcessingLogToRows(List<MessageProcessingLog> vdmList) {
        List<Map<String, Object>> rows = new ArrayList<>();

        vdmList.stream().forEach(vdm -> {
            rows.add(mapMessageProcessingLogRow(vdm));
        });

        return rows;
    }

    private Map<String, Object> mapMessageProcessingLogRow(final MessageProcessingLog e) {
        final Map<String, Object> m = new HashMap<>();
        m.put("MessageGuid", e.getMessageGuid());
        m.put("CorrelationId", e.getCorrelationId());
        // m.put("IntegrationFlowName", e.getIntegrationFlowName());
        m.put("Status", e.getStatus());
        m.put("CustomStatus", e.getCustomStatus());
        m.put("LogStart", normalizeToIso(e.getLogStart()));
        m.put("LogEnd", normalizeToIso(e.getLogEnd()));
        // m.put("IntegrationArtifact",
        // mapIntegrationArtifactRow(e.getIntegrationArtifact()));
        var integrationArtifact = e.getIntegrationArtifact();
        if (integrationArtifact != null) {
            m.put("IntegrationArtifact_Id", integrationArtifact.getId());
            m.put("IntegrationArtifact_Name", integrationArtifact.getName());
            m.put("IntegrationArtifact_Type", integrationArtifact.getType_2());
        }
        return m;
    }

    // --------------------------------------------------------------------
    // ----------------------- CustomHeaderProperties -----------------------
    // --------------------------------------------------------------------

    @On(event = CqnService.EVENT_READ, entity = MessageProcessingLogCustomHeaderProperties_.CDS_NAME)
    public void onReadMessageProcessingLogCustomHeaderProperties(final CdsReadEventContext ctx) {
        logger.info("Starting onReadMessageProcessingLogCustomHeaderProperties");
        logger.info(" with CQN: {}", ctx.getCqn());

        final CqnSelect select = ctx.getCqn();

        final CdsModel cdsModel = ctx.getModel();
        final CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(cdsModel);
        final AnalysisResult result = cqnAnalyzer.analyze(select.ref());

        final Map<String, Object> rootKeys = result.rootKeys();
        final String MessageGuid = (String) rootKeys.get("MessageGuid");

        final List<Map<String, Object>> rows = new ArrayList<>();
        ResultBuilder builder = null;

        if (MessageGuid != null && MessageGuid.trim().length() > 0) {
            final MessageProcessingLog mpl = this.messageProcessingLogsService
                    .getMessageProcessingLogByKey(
                            MessageGuid)
                    .executeRequest(getDestination());

            if (mpl != null) {
                // Fetch CustomHeaderProperties from the navigation
                List<MessageProcessingLogCustomHeaderProperty> entities = mpl.getCustomHeaderPropertiesOrFetch();

                builder = ResultBuilder.selectedRows(mapCustomHeaderPropertyToRow(entities)).inlineCount(
                        entities.size());
                if (builder != null) {
                    ctx.setResult(builder.result());
                } else {
                    ctx.setResult(rows);
                }
            }
        }
    }

    private List<Map<String, Object>> mapCustomHeaderPropertyToRow(
            List<MessageProcessingLogCustomHeaderProperty> vdmList) {
        List<Map<String, Object>> rows = new ArrayList<>();
        vdmList.stream().forEach(vdm -> {
            rows.add(mapCustomHeaderPropertyRow(vdm));
        });
        return rows;
    }

    private Map<String, Object> mapCustomHeaderPropertyRow(final MessageProcessingLogCustomHeaderProperty e) {
        final Map<String, Object> m = new HashMap<>();
        m.put("Id", e.getId());
        m.put("Name", e.getName());
        m.put("Value", e.getValue());
        m.put("Log", e.getLogIfPresent());
        return m;
    }

    // --------------------------------------------------------------------
    // ----------------------- MessageStoreEntries -----------------------
    // --------------------------------------------------------------------

    @On(event = CqnService.EVENT_READ, entity = MessageStoreEntries_.CDS_NAME)
    public void onReadMessageStoreEntries(final CdsReadEventContext ctx) {
        logger.info("Starting onReadMessageStoreEntries");
        logger.info(" with CQN: {}", ctx.getCqn());

        try {
            // 0. Is this a request for the stream property 'blob'?
            boolean isBlobStream = isBlobStreamRead(ctx);
            logger.info("isBlobStream: {}", isBlobStream);
            if (isBlobStream) {
                String id = extractIdFilter(ctx);
                logger.info("Blob stream read for Id={}", id);

                byte[] bytes = fetchViaUntypedOData(id); // see method below

                Map<String, Object> row = new HashMap<>();
                row.put("Id", id);
                // row.put("blob", bytes); // CAP will stream this for GET .../blob
                row.put("blob", new ByteArrayInputStream(bytes));
                ctx.setResult(Collections.singletonList(row));
                return;
            }

            // 1) Single-entity read? (key predicate present)
            final String idKey = extractIdFilter(ctx);
            logger.info("MessageStoreId: {}", idKey);
            if (idKey != null && !idKey.isEmpty()) {
                logger.info("MessageStore Single Read: {} ", idKey);
                final MessageStoreEntry e = this.messageStoreService
                        .getMessageStoreEntryByKey(idKey).executeRequest(getDestination());

                if (e != null) {
                    ctx.setResult(Collections.singletonList(mapMessageStoreEntryRow(e)));
                }
                return;
            }

            // 2) Navigation read with MessageGuid filter
            // Extract filter on MessageGuid if present
            final String messageGuidFilter = extractMessageGuidFilter(ctx);

            final List<Map<String, Object>> rows = new ArrayList<>();
            ResultBuilder builder = null;
            if (messageGuidFilter != null && !messageGuidFilter.isEmpty()) {
                // Fetch the parent MessageProcessingLog and get MessageStoreEntries from
                // navigation
                final MessageProcessingLog mpl = this.messageProcessingLogsService
                        .getMessageProcessingLogByKey(messageGuidFilter)
                        .executeRequest(getDestination());
                if (mpl != null) {
                    // Fetch MessageStoreEntries from the navigation
                    List<com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageStoreEntry> entities = mpl
                            .getMessageStoreEntriesOrFetch();

                    builder = ResultBuilder.selectedRows(
                            mapMplMessageStoreEntryToRow(entities)).inlineCount(
                                    entities.size());
                }
            }

            // ctx.setResult(rows);
            if (builder != null) {
                ctx.setResult(builder.result());
            } else {
                ctx.setResult(rows);
            }

        } catch (Exception e) {
            throw new RuntimeException("Typed VDM READ failed: " + e.getMessage(), e);
        }
    }

    private boolean isBlobStreamRead(CdsReadEventContext ctx) {
        logger.info("Starting isBlobStreamRead");
        logger.info(" with CQN: {}", ctx.getCqn());

        if (!(ctx.getCqn() instanceof CqnSelect)) {
            return false;
        }

        CqnSelect sel = (CqnSelect) ctx.getCqn();
        var items = sel.items(); // <-- modern replacement for columns()

        if (items == null || items.size() != 1) {
            return false;
        }

        // Minimal, version-agnostic check
        String itemStr = String.valueOf(items.get(0)) // e.g. "blob", "MessageStoreEntries.blob"
                .replace("\"", "")
                .trim();

        boolean isBlob = "blob".equalsIgnoreCase(itemStr)
                || itemStr.endsWith(".blob")
                || itemStr.endsWith("/blob")
                || itemStr.contains("blob"); // optional fallback

        logger.info("isBlob: {}", isBlob);

        return isBlob;
    }

    private String extractMessageGuidFilter(final CdsReadEventContext ctx) {
        logger.info("Starting extractMessageGuidFilter (CQN-based)");

        if (!(ctx.getCqn() instanceof CqnSelect)) {
            return null;
        }
        CqnSelect sel = (CqnSelect) ctx.getCqn();

        try {
            CqnAnalyzer analyzer = CqnAnalyzer.create(ctx.getModel());
            AnalysisResult ar = analyzer.analyze(sel.ref());

            Map<String, Object> rootKeys = ar.rootKeys();
            Object guid = rootKeys.get("MessageGuid");

            if (guid != null) {
                String guidStr = String.valueOf(guid);
                logger.info("extractMessageGuidFilter -> {}", guidStr);
                return guidStr;
            }

        } catch (Exception e) {
            logger.warn("CQN-based MessageGuid extraction failed: {}", e.getMessage());
        }

        return null;
    }

    private String extractIdFilter(final CdsReadEventContext ctx) {
        logger.info("Starting extractIdFilter (CQN-based)");

        if (!(ctx.getCqn() instanceof CqnSelect)) {
            return null;
        }
        CqnSelect sel = (CqnSelect) ctx.getCqn();

        try {
            CqnAnalyzer analyzer = CqnAnalyzer.create(ctx.getModel());
            AnalysisResult ar = analyzer.analyze(sel.ref());

            Map<String, Object> rootKeys = ar.rootKeys();
            Map<String, Object> targetKeys = ar.targetKeys();

            Object id = rootKeys.get("Id");
            if (id == null) {
                id = targetKeys.get("Id");
            }

            if (id != null) {
                String idStr = String.valueOf(id);
                logger.info("extractIdFilter -> {}", idStr);
                return idStr;
            }

        } catch (Exception e) {
            logger.warn("CQN-based Id extraction failed: {}", e.getMessage());
        }
        return null;
    }

    private byte[] fetchViaUntypedOData(String id) throws Exception {
        logger.info("Starting fetchViaUntypedOData for Id={}", id);
        // If your destination already includes /api/v1, set servicePath = "".
        String servicePath = "/api/v1";

        // Resource path: MessageStoreEntries('id')/$value (OData V2)
        ODataEntityKey key = new ODataEntityKey(ODataProtocol.V2).addKeyProperty("Id", id);
        ODataResourcePath path = ODataResourcePath.of("MessageStoreEntries", key).addSegment("$value");

        ODataRequestReadByKey req = new ODataRequestReadByKey(servicePath, path, null, ODataProtocol.V2);

        ODataRequestResultGeneric result = req
                .execute(HttpClientAccessor.getHttpClient(getDestination()));

        try (InputStream is = result.getHttpResponse().getEntity().getContent()) {
            return is.readAllBytes();
        }
    }

    private List<Map<String, Object>> mapMplMessageStoreEntryToRow(
            List<com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageStoreEntry> vdmList) {
        List<Map<String, Object>> rows = new ArrayList<>();

        vdmList.stream().forEach(vdm -> {
            rows.add(mapMessageStoreEntryRow(vdm));
        });
        return rows;
    }

    private Map<String, Object> mapMessageStoreEntryRow(
            final com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageStoreEntry e) {
        final Map<String, Object> m = new HashMap<>();
        m.put("Id", e.getId());
        m.put("MessageGuid", e.getMessageGuid());
        m.put("MessageStoreId", e.getMessageStoreId());
        m.put("TimeStamp", normalizeToIso(e.getTimeStamp()));
        m.put("HasAttachments", e.getHasAttachments());
        // Attachments and Properties are navigations, can be omitted or added as
        // deferred
        return m;
    }

    private Map<String, Object> mapMessageStoreEntryRow(final MessageStoreEntry e) {
        final Map<String, Object> m = new HashMap<>();
        m.put("Id", e.getId());
        m.put("MessageGuid", e.getMessageGuid());
        m.put("MessageStoreId", e.getMessageStoreId());
        m.put("TimeStamp", normalizeToIso(e.getTimeStamp()));
        m.put("HasAttachments", e.getHasAttachments());
        // Attachments and Properties are navigations, can be omitted or added as
        // deferred
        return m;
    }

    // --------------------------------------------------------------------
    // -------------------- IntegrationRuntimeArtifacts -------------------
    // --------------------------------------------------------------------

    @On(event = CqnService.EVENT_READ, entity = IntegrationRuntimeArtifacts_.CDS_NAME)
    public void onReadIntegrationRuntimeArtifacts(final CdsReadEventContext ctx) {
        // Implementation similar to above handlers
        logger.info("Starting onReadIntegrationRuntimeArtifacts");
        logger.info(" with CQN: {}", ctx.getCqn());

        final CqnSelect select = ctx.getCqn();

        IntegrationRuntimeArtifactFluentHelper fluentHelper = this.integrationContentService
                .getAllIntegrationRuntimeArtifact().select(
                        IntegrationRuntimeArtifact.ID,
                        IntegrationRuntimeArtifact.VERSION,
                        IntegrationRuntimeArtifact.NAME,
                        IntegrationRuntimeArtifact.TYPE_2,
                        IntegrationRuntimeArtifact.DEPLOYED_BY,
                        IntegrationRuntimeArtifact.DEPLOYED_ON,
                        IntegrationRuntimeArtifact.STATUS);

        IntegrationRuntimeArtifactsCqnVisitor visitor = new IntegrationRuntimeArtifactsCqnVisitor(fluentHelper);
        select.accept(visitor);

        final List<IntegrationRuntimeArtifact> entities = fluentHelper.executeRequest(getDestination());
        // final FluentHelperCount fluentHelperCount = fluentHelper.count();
        // final long count = fluentHelperCount.executeRequest(getDestination());
        final long count = entities.size();

        ctx.setResult(ResultBuilder.selectedRows(
                mapIntegrationRuntimeArtifactToRows(entities)).inlineCount(count).result());
    }

    private List<Map<String, Object>> mapIntegrationRuntimeArtifactToRows(List<IntegrationRuntimeArtifact> vdmList) {
        List<Map<String, Object>> rows = new ArrayList<>();

        vdmList.stream().forEach(vdm -> {
            rows.add(mapIntegrationRuntimeArtifactRow(vdm));
        });

        return rows;
    }

    private Map<String, Object> mapIntegrationRuntimeArtifactRow(final IntegrationRuntimeArtifact e) {
        final Map<String, Object> m = new HashMap<>();
        m.put("Id", e.getId());
        m.put("Version", e.getVersion());
        m.put("Name", e.getName());
        m.put("Type", e.getType());
        m.put("DeployedBy", e.getDeployedBy());
        m.put("DeployedOn", e.getDeployedOn());
        m.put("Status", e.getStatus());
        return m;
    }

    /** Normalize various OData V2/VDM time representations to ISO-8601 (UTC). */
    private static Object normalizeToIso(final Object v) {
        if (v == null)
            return null;
        if (v instanceof Instant)
            return ((Instant) v).toString();
        if (v instanceof OffsetDateTime)
            return ((OffsetDateTime) v).toInstant().toString();
        if (v instanceof ZonedDateTime)
            return ((ZonedDateTime) v).toInstant().toString();
        if (v instanceof LocalDateTime)
            return ((LocalDateTime) v).toInstant(ZoneOffset.UTC).toString();
        if (v instanceof java.util.Date)
            return ((java.util.Date) v).toInstant().toString();
        if (v instanceof Number)
            return Instant.ofEpochMilli(((Number) v).longValue()).toString();
        if (v instanceof String) {
            final String s = (String) v;
            final Matcher m = ODATA_V2_DATE.matcher(s);
            if (m.matches()) {
                final long ms = Long.parseLong(m.group(1));
                return Instant.ofEpochMilli(ms).toString();
            }
            if (looksLikeIso(s))
                return s;
        }
        return v;
    }

    private static boolean looksLikeIso(final String s) {
        return s.contains("T") && s.length() >= 20;
    }

    // --------------------------------------------------------------------
    // ---------------------------- Unused Section ------------------------
    // --------------------------------------------------------------------

}
