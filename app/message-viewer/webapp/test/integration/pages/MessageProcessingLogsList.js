sap.ui.define(['sap/fe/test/ListReport'], function(ListReport) {
    'use strict';

    var CustomPageDefinitions = {
        actions: {},
        assertions: {}
    };

    return new ListReport(
        {
            appId: 'com.binlah.sap.btp.cpi.messageviewer',
            componentId: 'MessageProcessingLogsList',
            contextPath: '/MessageProcessingLogs'
        },
        CustomPageDefinitions
    );
});