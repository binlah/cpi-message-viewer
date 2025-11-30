package com.binlah.sap.btp.cpi.cpi_message_viewer.cqn;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binlah.sap.cap.vdm.namespaces.integrationcontent.IntegrationRuntimeArtifact;
import com.binlah.sap.cap.vdm.namespaces.integrationcontent.IntegrationRuntimeArtifactFluentHelper;
import com.sap.cds.ql.cqn.CqnComparisonPredicate;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnVisitor;
import com.sap.cloud.sdk.datamodel.odata.helper.Order;

public class IntegrationRuntimeArtifactsCqnVisitor implements CqnVisitor {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationRuntimeArtifactsCqnVisitor.class);

    private IntegrationRuntimeArtifactFluentHelper fluentHelper;

    private String entityName;
    private List<String> selectFields = new ArrayList<>();
    private Integer top;
    private Integer skip;
    private boolean count;

    public IntegrationRuntimeArtifactsCqnVisitor(IntegrationRuntimeArtifactFluentHelper fluentHelper) {
        this.fluentHelper = fluentHelper;
    }

    @Override
    public void visit(CqnSelect select) {
        logger.debug("Visiting CqnSelect");

        // Extract entity name
        if (select.ref() != null && select.ref().targetSegment() != null) {
            entityName = select.ref().targetSegment().id();
            logger.debug(" > Entity: " + entityName);
        }

        // Extract SELECT fields
        if (select.items() != null && !select.items().isEmpty()) {
            select.items().forEach(item -> {
                if (item.isRef()) {
                    selectFields.add(item.asRef().segments().get(0).id());
                }
            });
            logger.debug(" > Select fields: " + selectFields);
        }

        // Extract TOP (limit)
        long topValue = select.top();
        top = (int) topValue;
        if (top != null) {
            logger.debug(" > Top: " + top);
            fluentHelper = fluentHelper.top(top);
        }

        // Extract SKIP (offset)

        long skipValue = select.skip();
        skip = (int) skipValue;
        if (skip != null) {
            logger.debug(" > Skip: " + skip);
            fluentHelper = fluentHelper.skip(skip);
        }

        select.orderBy().forEach(orderItem -> {
            logger.debug(" > Order By: " + orderItem);

        });

        fluentHelper = fluentHelper.orderBy(IntegrationRuntimeArtifact.ID,
                Order.ASC);

        // Check if count is requested
        count = select.hasInlineCount();
        logger.debug(" > hasInlineCount: " + count);
    }

    @Override
    public void visit(CqnComparisonPredicate compPred) {
        logger.debug("Visiting CqnComparisonPredicate: " + compPred);

    }
}
