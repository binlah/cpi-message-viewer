sap.ui.define(['sap/fe/test/ObjectPage'], function(ObjectPage) {
    'use strict';

    var CustomPageDefinitions = {
        actions: {},
        assertions: {}
    };

    return new ObjectPage(
        {
            appId: 'com.binlah.sap.btp.cpi.messageviewer',
            componentId: 'MessageStoreEntriesObjectPage',
            contextPath: '/MessageProcessingLogs/MessageStoreEntries'
        },
        CustomPageDefinitions
    );
});