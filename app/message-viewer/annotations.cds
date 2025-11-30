using RemoteService as service from '../../srv/remote-service';

annotate service.MessageProcessingLogs with @(
    UI.FieldGroup #GeneratedGroup: {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Value: IntegrationArtifact_Name,
                Label: 'IntegrationArtifact_Name',
            },
            {
                $Type: 'UI.DataField',
                Label: 'MessageGuid',
                Value: MessageGuid,
            },
            {
                $Type: 'UI.DataField',
                Label: 'CorrelationId',
                Value: CorrelationId,
            },
            {
                $Type: 'UI.DataField',
                Label: 'LogStart',
                Value: LogStart,
            },
            {
                $Type: 'UI.DataField',
                Label: 'LogEnd',
                Value: LogEnd,
            },
            {
                $Type: 'UI.DataField',
                Label: 'Status',
                Value: Status,
            },
            {
                $Type: 'UI.DataField',
                Label: 'CustomStatus',
                Value: CustomStatus,
            },
        ],
    },
    UI.Facets                    : [
        {
            $Type : 'UI.ReferenceFacet',
            ID    : 'GeneratedFacet1',
            Label : 'General Information',
            Target: '@UI.FieldGroup#GeneratedGroup',
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Customer Headers',
            ID    : 'CustomerHeaders',
            Target: 'CustomHeaderProperties/@UI.LineItem#CustomerHeaders',
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Message Store Entries',
            ID    : 'MessageStoreEntries',
            Target: 'MessageStoreEntries/@UI.LineItem#MessageStoreEntries',
        },
    ],
    UI.LineItem                  : [
        {
            $Type: 'UI.DataField',
            Value: IntegrationArtifact_Name,
            Label: 'IntegrationArtifact_Name',
        },
        {
            $Type: 'UI.DataField',
            Label: 'LogStart',
            Value: LogStart,
        },
        {
            $Type: 'UI.DataField',
            Label: 'LogEnd',
            Value: LogEnd,
        },
        {
            $Type: 'UI.DataField',
            Value: Status,
        },
        {
            $Type: 'UI.DataField',
            Value: CustomStatus,
        },
        {
            $Type: 'UI.DataField',
            Label: 'MessageGuid',
            Value: MessageGuid,
        },
        {
            $Type: 'UI.DataField',
            Value: CorrelationId,
            Label: 'CorrelationId',
        },
    ],
    UI.SelectionFields           : [
        IntegrationArtifact_Id,
        BeginSearchTime,
        EndSearchTime,
        CustomStatus,
        customHeaderKV,
        MessageGuid,
        CorrelationId,
        Status,
    ],
    UI.HeaderInfo                : {
        Title         : {
            $Type: 'UI.DataField',
            Value: IntegrationArtifact_Name,
        },
        TypeName      : '',
        TypeNamePlural: '',
    },
);

// annotate service.MessageProcessingLogs with {
//     IntegrationFlowName @Common.Label: 'IntegrationFlowName'
// };

annotate service.MessageProcessingLogs with {
    CustomStatus @Common.Label: '{i18n>CustomStatus}'
};

annotate service.MessageProcessingLogs with {
    MessageGuid @Common.Label: '{i18n>Messageguid}'
};

annotate service.MessageProcessingLogs with {
    Status @Common.Label: '{i18n>Status}'
};

annotate service.MessageProcessingLogCustomHeaderProperties with @(UI.LineItem #CustomerHeaders: [
    {
        $Type: 'UI.DataField',
        Value: Name,
        Label: 'Name',
    },
    {
        $Type: 'UI.DataField',
        Value: Value,
        Label: 'Value',
    },
]);

annotate service.MessageStoreEntries with @(
    UI.LineItem #MessageStoreEntries: [
        {
            $Type: 'UI.DataField',
            Value: MessageStoreId,
            Label: 'MessageStoreId',
        },
        {
            $Type: 'UI.DataField',
            Value: TimeStamp,
            Label: 'TimeStamp',
        },
    ],
    UI.HeaderInfo                   : {
        Title         : {
            $Type: 'UI.DataField',
            Value: MessageStoreId,
        },
        TypeName      : '',
        TypeNamePlural: '',
    },
);

annotate service.MessageProcessingLogs : IntegrationArtifact.Id with @(
    Common.Label                   : '{i18n>IntegrationArtifact}',
    Common.Text                    : IntegrationArtifact_Name,
    Common.ValueList               : {
        $Type         : 'Common.ValueListType',
        CollectionPath: 'IntegrationRuntimeArtifacts',
        Parameters    : [{
            $Type            : 'Common.ValueListParameterInOut',
            LocalDataProperty: IntegrationArtifact_Id,
            ValueListProperty: 'Id',
        }, ],
    },
    Common.ValueListWithFixedValues: true,
);

annotate service.MessageProcessingLogs with {
    customHeaderKV @Common.Label: '{i18n>CustomHeader}'
};

annotate service.MessageProcessingLogs with {
    LogStart @Common.Label: 'LogStart'
};

annotate service.MessageProcessingLogs with {
    LogEnd @Common.Label: 'LogEnd'
};

annotate service.MessageProcessingLogs with {
    BeginSearchTime @Common.Label: '{i18n>BeginTime}'
};

annotate service.MessageProcessingLogs with {
    EndSearchTime @Common.Label: '{i18n>EndTime}'
};

annotate service.MessageProcessingLogs with {
    CorrelationId @Common.Label: '{i18n>Correlationid}'
};

annotate service.MessageProcessingLogs with {
    LogStart               @UI.HiddenFilter: true;
    LogEnd                 @UI.HiddenFilter: true;
    CustomHeaderProperties @UI.HiddenFilter: true;
    MessageStoreEntries    @UI.HiddenFilter: true;
};

annotate RemoteService.MessageProcessingLogs with @(Capabilities: {FilterRestrictions: {FilterExpressionRestrictions: [
    {
        Property          : 'BeginSearchTime',
        AllowedExpressions: 'SingleValue'
    },
    {
        Property          : 'EndSearchTime',
        AllowedExpressions: 'SingleValue'
    },
    {
        Property          : 'MessageGuid',
        AllowedExpressions: 'SingleValue'
    },
    {
        Property          : 'CorrelationId',
        AllowedExpressions: 'SingleValue'
    },
    {
        Property          : 'Status',
        AllowedExpressions: 'SingleValue'
    },
    {
        Property          : 'CustomStatus',
        AllowedExpressions: 'SingleValue'
    },
    {
        Property          : 'customHeaderKV',
        AllowedExpressions: 'SingleValue'
    }
]}});
