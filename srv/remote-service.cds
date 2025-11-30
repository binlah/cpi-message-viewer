using {MessageProcessingLogs as extMpl
                                      // ,MessageProcessingLogs.IntegrationArtifact as ExtIntegrationArtifact
                                } from './external/MessageProcessingLogs';
using MessageStore as extMs from './external/MessageStore';
using IntegrationContent as extIc from './external/IntegrationContent';

service RemoteService @(path: '/remote') {
    // type IntegrationArtifact : ExtIntegrationArtifact;

    // @readonly
    @cds.persistence.skip
    @Capabilities.InsertRestrictions.Insertable: false
    @Capabilities.UpdateRestrictions.Updatable : false
    @Capabilities.DeleteRestrictions.Deletable : false
    entity MessageProcessingLogs                      as
        projection on extMpl.MessageProcessingLogs {
            @Capabilities.FilterRestrictions.Filterable: true
            MessageGuid,
            @Capabilities.FilterRestrictions.Filterable: true
            CorrelationId,
            // LogStart               : DateTime,
            // LogEnd                 : DateTime,
            LogStart                : Timestamp,
            LogEnd                  : Timestamp,
            // IntegrationFlowName,
            Status,
            IntegrationArtifact,
            CustomStatus,
            CustomHeaderProperties,

            // 👇 ensure nav bindings point to your local projections
            // CustomHeaderProperties : redirected to MessageProcessingLogCustomHeaderProperties,
            MessageStoreEntries,

            // Optional single-box variant: users type "key=value"
            @Core.Computed                             : true
            @Capabilities.FilterRestrictions.Filterable: true
            // @UI.Hidden                                 : true
            virtual customHeaderKV  : String,

            @Core.Computed                             : true
            @Capabilities.FilterRestrictions.Filterable: true
            virtual BeginSearchTime : Timestamp,

            @Core.Computed                             : true
            @Capabilities.FilterRestrictions.Filterable: true
            virtual EndSearchTime   : Timestamp
        };

    @cds.persistence.skip
    @Capabilities.InsertRestrictions.Insertable: false
    @Capabilities.UpdateRestrictions.Updatable : false
    @Capabilities.DeleteRestrictions.Deletable : false
    @Capabilities.Countable                    : false
    // @(path: 'MessageProcessingLogCustomHeaderProperties')
    entity MessageProcessingLogCustomHeaderProperties as
        projection on extMpl.MessageProcessingLogCustomHeaderProperties {
            Id,
            Name,
            Value,
            Log
        };

    @cds.persistence.skip
    @Capabilities.InsertRestrictions.Insertable: false
    @Capabilities.UpdateRestrictions.Updatable : false
    @Capabilities.DeleteRestrictions.Deletable : false
    @Capabilities.Countable                    : false
    // @(path: 'MessageStoreEntries')
    entity MessageStoreEntries                        as
        projection on extMpl.MessageStoreEntries {
            Id,
            MessageGuid,
            MessageStoreId,
            TimeStamp : Timestamp,
            HasAttachments,
            @Core.MediaType: 'application/octet-stream'
            blob,
            Attachments,
            Properties
        };


    @cds.persistence.skip
    @Capabilities.InsertRestrictions.Insertable: false
    @Capabilities.UpdateRestrictions.Updatable : false
    @Capabilities.DeleteRestrictions.Deletable : false
    @Capabilities.Countable                    : false
    entity IntegrationRuntimeArtifacts                as
        projection on extIc.IntegrationRuntimeArtifacts {
            Id,
            Version,
            Name,
            Type,
            DeployedBy,
            DeployedOn,
            Status,
        };

    annotate RemoteService.MessageProcessingLogs with @Capabilities.SearchRestrictions: {Searchable: false};
    annotate RemoteService.MessageProcessingLogCustomHeaderProperties with @Capabilities.SearchRestrictions: {Searchable: false};
    annotate RemoteService.MessageStoreEntries with @Capabilities.SearchRestrictions: {Searchable: false};

    annotate RemoteService with @(requires: 'MessageViewer');

// Add these annotations to override the OData type
// annotate RemoteService.MessageProcessingLogs with {
//     @odata.Type: 'Edm.DateTimeOffset'
//     LogStart;

//     @odata.Type: 'Edm.DateTimeOffset'
//     LogEnd;
// };

// Keep your existing FilterRestrictions
// annotate RemoteService.MessageProcessingLogs with @(Capabilities: {FilterRestrictions: {FilterExpressionRestrictions: [
//     {
//         Property          : 'BeginSearchTime',
//         AllowedExpressions: 'SingleValue'
//     },
//     {
//         Property          : 'EndSearchTime',
//         AllowedExpressions: 'SingleValue'
//     },
//     {
//         Property          : 'MessageGuid',
//         AllowedExpressions: 'SingleValue'
//     },
//     {
//         Property          : 'CorrelationId',
//         AllowedExpressions: 'SingleValue'
//     }
// ]}});
}
