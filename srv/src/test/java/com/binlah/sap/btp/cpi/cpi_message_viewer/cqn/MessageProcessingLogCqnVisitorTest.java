package com.binlah.sap.btp.cpi.cpi_message_viewer.cqn;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.IntegrationArtifact;
import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageProcessingLog;
import com.binlah.sap.cap.vdm.namespaces.messageprocessinglogs.MessageProcessingLogFluentHelper;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;

public class MessageProcessingLogCqnVisitorTest {

        private static final Logger logger = LoggerFactory.getLogger(MessageProcessingLogCqnVisitor.class);

        public static void main(String[] args) {
                // testMultipleOrConditions();
                testComplexMixedConditions();
                // testMultipleFields();
        }

        /**
         * Test Case 1: Multiple OR conditions on same field
         * Query: WHERE IntegrationArtifact_Id = 'IF_385' OR IntegrationArtifact_Id =
         * 'IF_565'
         */
        public static void testMultipleOrConditions() {
                logger.debug("=== Test Case 1: Multiple OR Conditions ===");

                // This simulates your OData query:
                // $filter=(IntegrationArtifact_Id eq 'IF_385' or
                // IntegrationArtifact_Id eq 'IF_565')

                CqnSelect select = Select.from("MessageProcessingLogs")
                                .columns("IntegrationArtifact_Name", "LogEnd", "LogStart", "MessageGuid")
                                .where(b -> b.get("IntegrationArtifact_Id").eq("IF_385")
                                                .or(b.get("IntegrationArtifact_Id")
                                                                .eq("IF_565")));
                // .top(25)
                // .skip(0);

                logger.debug("select: " + select);

                // Process with visitor
                MessageProcessingLogFluentHelper helper = new MessageProcessingLogFluentHelper(
                                "MessageProcessingLogs", "")
                                .select(
                                                MessageProcessingLog.MESSAGE_GUID,
                                                MessageProcessingLog.CORRELATION_ID,
                                                MessageProcessingLog.LOG_START,
                                                MessageProcessingLog.LOG_END,
                                                MessageProcessingLog.STATUS,
                                                MessageProcessingLog.field("IntegrationArtifact",
                                                                IntegrationArtifact.class),
                                                MessageProcessingLog.CUSTOM_STATUS);
                MessageProcessingLogCqnVisitor visitor = new MessageProcessingLogCqnVisitor(helper);
                select.accept(visitor);

                // ExpressionFluentHelper<MessageProcessingLog> artifactIdFilter =
                // MessageProcessingLog
                // .field("IntegrationArtifact/Id", String.class).eq("artifactId1");

                // artifactIdFilter.or(
                // MessageProcessingLog.field("IntegrationArtifact/Id",
                // String.class).eq("artifactId2"));

                // helper.filter(artifactIdFilter);
        }

        private static void testComplexMixedConditions() {
                logger.debug("=== Test Case 2: Complex Mixed Conditions ===");

                // Parse the datetime
                Instant logEndTime = Instant.parse("2025-10-31T17:00:00Z");
                Instant logStartTime = Instant.parse("2025-11-14T16:59:00Z");

                CqnSelect select = Select.from("MessageProcessingLogs")
                                .columns("MessageGuid", "LogStart", "LogEnd", "IntegrationArtifact_Name",
                                                "CustomStatus")
                                .where(b -> b.get("LogEnd").ge(logEndTime)
                                                .and(b.get("LogStart").le(logStartTime))
                                                .and(b.get("IntegrationArtifact_Id")
                                                                .eq("IF_562")
                                                                .or(b.get("IntegrationArtifact_Id")
                                                                                .eq("IF_563"))
                                                                .or(b.get("IntegrationArtifact_Id").eq(
                                                                                "IF_564")))
                                                .and(b.get("customHeaderKV")
                                                                .eq("'legacy-name' eq 'K2'")))
                                .orderBy(o -> o.get("LogEnd").desc())
                                .limit(50, 50);

                logger.debug("select: " + select);

                // Process with visitor
                MessageProcessingLogFluentHelper helper = new MessageProcessingLogFluentHelper(
                                "MessageProcessingLogs", "")
                                .select(
                                                MessageProcessingLog.MESSAGE_GUID,
                                                MessageProcessingLog.CORRELATION_ID,
                                                MessageProcessingLog.LOG_START,
                                                MessageProcessingLog.LOG_END,
                                                MessageProcessingLog.STATUS,
                                                MessageProcessingLog.field("IntegrationArtifact",
                                                                IntegrationArtifact.class),
                                                MessageProcessingLog.CUSTOM_STATUS);
                MessageProcessingLogCqnVisitor visitor = new MessageProcessingLogCqnVisitor(helper);
                select.accept(visitor);
        }
}
