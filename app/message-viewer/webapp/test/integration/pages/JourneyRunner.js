sap.ui.define([
    "sap/fe/test/JourneyRunner",
	"com/binlah/sap/btp/cpi/messageviewer/test/integration/pages/MessageProcessingLogsList",
	"com/binlah/sap/btp/cpi/messageviewer/test/integration/pages/MessageProcessingLogsObjectPage",
	"com/binlah/sap/btp/cpi/messageviewer/test/integration/pages/MessageStoreEntriesObjectPage"
], function (JourneyRunner, MessageProcessingLogsList, MessageProcessingLogsObjectPage, MessageStoreEntriesObjectPage) {
    'use strict';

    var runner = new JourneyRunner({
        launchUrl: sap.ui.require.toUrl('com/binlah/sap/btp/cpi/messageviewer') + '/test/flpSandbox.html#combinlahsapbtpcpimessageviewe-tile',
        pages: {
			onTheMessageProcessingLogsList: MessageProcessingLogsList,
			onTheMessageProcessingLogsObjectPage: MessageProcessingLogsObjectPage,
			onTheMessageStoreEntriesObjectPage: MessageStoreEntriesObjectPage
        },
        async: true
    });

    return runner;
});

