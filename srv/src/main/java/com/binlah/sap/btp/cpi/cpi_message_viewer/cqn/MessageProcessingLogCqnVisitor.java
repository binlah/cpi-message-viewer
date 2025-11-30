package com.binlah.sap.btp.cpi.cpi_message_viewer.cqn;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cds.ql.cqn.CqnComparisonPredicate;
import com.sap.cds.ql.cqn.CqnConnectivePredicate;
import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnLiteral;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnValue;
import com.sap.cds.ql.cqn.CqnVisitor;
import com.sap.cloud.sdk.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.helper.Order;
import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageProcessingLog;
import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageProcessingLogFluentHelper;

public class MessageProcessingLogCqnVisitor implements CqnVisitor {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingLogCqnVisitor.class);

    private Map<String, Object> parameters = new HashMap<>();

    private boolean findById = false;
    // Store query components
    private String entityName;
    private List<String> selectFields = new ArrayList<>();
    private List<String> integrationArtifactList = new ArrayList<>();
    private Integer top;
    private Integer skip;
    private boolean count;

    private MessageProcessingLogFluentHelper fluentHelper;
    private ExpressionFluentHelper<MessageProcessingLog> mainFilter;
    private ExpressionFluentHelper<MessageProcessingLog> integrationArtifactIdFilter;

    private String filterCustomHeaderProperties;

    // public MessageProcessingLogCqnVisitor() {
    // }

    public MessageProcessingLogCqnVisitor(MessageProcessingLogFluentHelper helper) {
        this.fluentHelper = helper;
    }

    @Override
    public void visit(CqnSelect select) {
        logger.debug("Visiting CqnSelect");

        // Extract entity name
        if (select.ref() != null && select.ref().targetSegment() != null) {
            entityName = select.ref().targetSegment().id();
            logger.debug("  > Entity: " + entityName);
        }

        // Extract SELECT fields
        if (select.items() != null && !select.items().isEmpty()) {
            select.items().forEach(item -> {
                if (item.isRef()) {
                    selectFields.add(item.asRef().segments().get(0).id());
                }
            });
            logger.debug("  > Select fields: " + selectFields);
        }

        // Extract WHERE clause
        // select.where().ifPresent(predicate -> predicate.accept(this));

        mainFilter = (integrationArtifactIdFilter != null)
                ? ((mainFilter == null) ? integrationArtifactIdFilter : mainFilter.and(integrationArtifactIdFilter))
                : mainFilter;

        if (mainFilter != null) {
            fluentHelper = fluentHelper.filter(mainFilter);
        }

        // Extract TOP (limit)
        long topValue = select.top();
        top = (int) topValue;
        if (top != null) {
            logger.debug("  > Top: " + top);
            fluentHelper = fluentHelper.top(top);
        }

        // Extract SKIP (offset)

        long skipValue = select.skip();
        skip = (int) skipValue;
        if (skip != null) {
            logger.debug("  > Skip: " + skip);
            fluentHelper = fluentHelper.skip(skip);
        }

        select.orderBy().forEach(orderItem -> {
            logger.debug("  > Order By: " + orderItem);

        });

        fluentHelper = fluentHelper.orderBy(MessageProcessingLog.LOG_START,
                Order.DESC);

        // Check if count is requested
        count = select.hasInlineCount();
        logger.debug("  > hasInlineCount: " + count);
    }

    @Override
    public void visit(CqnPredicate predicate) {
        // This is a fallback for any predicate type not explicitly handled
        logger.debug("Visiting generic CqnPredicate (fallback): " + predicate);
    }

    /**
     * Visit comparison predicates (e.g., field = value, field > value)
     * This is called directly by the visitor pattern when it encounters a
     * CqnComparisonPredicate
     */
    @Override
    public void visit(CqnComparisonPredicate compPred) {
        logger.debug("Visiting CqnComparisonPredicate: " + compPred);

        // Get left side (usually field name)
        CqnValue left = compPred.left();
        if (left instanceof CqnElementRef) {
            CqnElementRef ref = (CqnElementRef) left;
            String fieldName = ref.segments().get(0).id();

            // Get operator
            // String operator = getOperator(compPred);
            String operator = compPred.operator().toString();

            // Get right side (value)
            CqnValue right = compPred.right();
            if (right instanceof CqnLiteral) {
                CqnLiteral<?> literal = (CqnLiteral<?>) right;
                Object value = literal.value();

                if (fieldName.equals("MessageGuid")) {
                    mainFilter = (mainFilter == null)
                            ? MessageProcessingLog.MESSAGE_GUID.eq(value.toString())
                            : mainFilter.and(MessageProcessingLog.MESSAGE_GUID.eq(value.toString()));

                    if (operator.equalsIgnoreCase("=")) {
                        findById = true;
                    }

                } else if (fieldName.equals("IntegrationArtifact_Id")) {
                    integrationArtifactList.add(value.toString());

                    // if (artifactIdFilter == null) {
                    // artifactIdFilter = MessageProcessingLog
                    // .field("IntegrationArtifact/Id", String.class).eq(value.toString());
                    // } else {
                    // artifactIdFilter.or(
                    // MessageProcessingLog.field("IntegrationArtifact/Id", String.class)
                    // .eq(value.toString()));
                    // }
                    integrationArtifactIdFilter = (integrationArtifactIdFilter == null)
                            ? MessageProcessingLog.field("IntegrationArtifact/Id", String.class).eq(value.toString())
                            : integrationArtifactIdFilter
                                    .or(MessageProcessingLog.field("IntegrationArtifact/Id", String.class)
                                            .eq(value.toString()));

                } else if (fieldName.equals("BeginSearchTime")) {
                    Instant instant = Instant.parse(value.toString());
                    final LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
                    mainFilter = (mainFilter == null)
                            ? MessageProcessingLog.LOG_END.ge(dateTime)
                            : mainFilter.and(MessageProcessingLog.LOG_END.ge(dateTime));

                } else if (fieldName.equals("EndSearchTime")) {
                    Instant instant = Instant.parse(value.toString());
                    final LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
                    mainFilter = (mainFilter == null)
                            ? MessageProcessingLog.LOG_START.lt(dateTime)
                            : mainFilter.and(MessageProcessingLog.LOG_START.lt(dateTime));

                } else if (fieldName.equals("CustomStatus")) {
                    mainFilter = (mainFilter == null)
                            ? MessageProcessingLog.CUSTOM_STATUS.eq(value.toString())
                            : mainFilter.and(MessageProcessingLog.CUSTOM_STATUS.eq(value.toString()));

                } else if (fieldName.equals("customHeaderKV")) {
                    filterCustomHeaderProperties = convertToEqualsFormat(value.toString());
                    fluentHelper = fluentHelper
                            .withQueryParameter("filterCustomHeaderProperties", filterCustomHeaderProperties);

                } else if (fieldName.equals("CorrelationId")) {
                    mainFilter = (mainFilter == null)
                            ? MessageProcessingLog.CORRELATION_ID.eq(value.toString())
                            : mainFilter.and(MessageProcessingLog.CORRELATION_ID.eq(value.toString()));

                } else {
                    parameters.put(fieldName, value);
                }
                logger.debug("  > Comparison: " + fieldName + " " + operator + " " + value);
            }
        }
    }

    /**
     * Converts a string from format "param=value" to "'param' eq 'value'"
     * 
     * @param input the input string in format "param=value"
     * @return the converted string in format "'param' eq 'value'"
     * @throws IllegalArgumentException if input is null or doesn't contain '='
     */
    public String convertToEqualsFormat(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        int equalsIndex = input.indexOf('=');
        if (equalsIndex == -1) {
            throw new IllegalArgumentException("Input must contain '=' character");
        }

        String param = input.substring(0, equalsIndex);
        String value = input.substring(equalsIndex + 1);

        return String.format("'%s' eq '%s'", param, value);
    }

    /**
     * Visit connective predicates (AND, OR)
     * This is called directly by the visitor pattern when it encounters a
     * CqnConnectivePredicate
     */
    @Override
    public void visit(CqnConnectivePredicate connPred) {
        logger.debug("Visiting CqnConnectivePredicate: " + connPred);

        String operator = getConnectiveOperator(connPred);
        logger.debug("  > Connective: " + operator);

        // Visit all predicates in the connective
        // for (CqnPredicate predicate : connPred.predicates()) {
        // predicate.accept(this);
        // }
    }

    // @Override
    public void visit(CqnValue value) {
        logger.debug("Visiting CqnValue: " + value);

        if (value instanceof CqnLiteral) {
            CqnLiteral<?> literal = (CqnLiteral<?>) value;
            logger.debug("  > Literal value: " + literal.value());
        }
    }

    /**
     * Get SQL operator from CQN comparison predicate
     */
    private String getOperator(CqnComparisonPredicate predicate) {
        switch (predicate.operator()) {
            case EQ:
                return "=";
            case NE:
                return "!=";
            case LT:
                return "<";
            case LE:
                return "<=";
            case GT:
                return ">";
            case GE:
                return ">=";
            default:
                return "=";
        }
    }

    /**
     * Get SQL connective operator from CQN connective predicate
     */
    private String getConnectiveOperator(CqnConnectivePredicate predicate) {
        switch (predicate.operator()) {
            case AND:
                return "AND";
            case OR:
                return "OR";
            default:
                return "AND";
        }
    }

    // Getters for accessing the parsed query components
    public MessageProcessingLogFluentHelper getFluentHelper() {
        return fluentHelper;
    }

    public boolean isFindById() {
        return findById;
    }
}
