/* checksum : 38fa7944818e17c750316d7ddf85f7bd */
@cds.external              : true
@m.IsDefaultEntityContainer: 'true'
service MessageProcessingLogs {
  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageStoreEntries {
    key Id             : LargeString not null;
        MessageGuid    : LargeString;
        MessageStoreId : LargeString;

        @odata.Type    : 'Edm.DateTime'
        TimeStamp      : DateTime;
        HasAttachments : Boolean;

        @Core.MediaType: 'application/octet-stream'
        blob           : LargeBinary;
        // Attachments    : Association to many MessageStoreEntryAttachments {}; // Binla removed
        Attachments    : Composition of many MessageStoreEntryAttachments // Binla added
                           on Attachments.Id = Id;
        // Properties     : Association to many MessageStoreEntryProperties {}; // Binla removed
        Properties     : Composition of many MessageStoreEntryProperties
                           on Properties.MessageId = Id; // Binla added
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity TraceMessages {
    key TraceId     : Integer64 not null;
        MplId       : LargeString not null;
        ModelStepId : LargeString;
        PayloadSize : Integer64;
        MimeType    : LargeString;

        @Core.MediaType: 'application/octet-stream'
        blob        : LargeBinary;
        Properties  : Association to many TraceMessageProperties {};
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageProcessingLogRuns {
    key Id           : LargeString not null;

        @odata.Type: 'Edm.DateTime'
        RunStart     : DateTime;

        @odata.Type: 'Edm.DateTime'
        RunStop      : DateTime;
        LogLevel     : LargeString;
        OverallState : LargeString;
        ProcessId    : LargeString;
        RunSteps     : Association to many MessageProcessingLogRunSteps {};
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageProcessingLogRunSteps {
    key RunId             : LargeString not null;
    key ChildCount        : Integer not null;

        @odata.Type: 'Edm.DateTime'
        StepStart         : DateTime;

        @odata.Type: 'Edm.DateTime'
        StepStop          : DateTime;
        StepId            : LargeString;
        ModelStepId       : LargeString;
        BranchId          : LargeString;
        Status            : LargeString;
        Error             : LargeString;
        Activity          : LargeString;
        RunStepProperties : Association to many MessageProcessingLogRunStepProperties {};
        TraceMessages     : Association to many TraceMessages {};
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageStoreEntryAttachmentProperties {
    key AttachmentId : LargeString not null;
    key Name         : LargeString not null;
        Value        : LargeString;
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageStoreEntryAttachments {
    key Id          : LargeString not null;
        Name        : LargeString;
        ContentType : LargeString;

        @Core.MediaType: 'application/octet-stream'
        blob        : LargeBinary;
        // Properties  : Association to many MessageStoreEntryAttachmentProperties {}; // Binla removed
        Properties  : Composition of many MessageStoreEntryAttachmentProperties
                        on Properties.AttachmentId = Id; // Binla add
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageStoreEntryProperties {
    key MessageId : LargeString not null;
    key Name      : LargeString not null;
        Value     : LargeString;
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageProcessingLogs {
    key MessageGuid            : LargeString not null;
        CorrelationId          : LargeString;
        ApplicationMessageId   : LargeString;
        ApplicationMessageType : LargeString;

        @odata.Type: 'Edm.DateTime'
        LogStart               : DateTime;

        @odata.Type: 'Edm.DateTime'
        LogEnd                 : DateTime;
        Sender                 : LargeString;
        Receiver               : LargeString;
        IntegrationFlowName    : LargeString;
        Status                 : LargeString;
        AlternateWebLink       : LargeString;
        IntegrationArtifact    : IntegrationArtifact not null;
        LogLevel               : LargeString;
        CustomStatus           : LargeString;
        TransactionId          : LargeString;
        PreviousComponentName  : LargeString;
        LocalComponentName     : LargeString;
        OriginComponentName    : LargeString;
        // CustomHeaderProperties : Association to many MessageProcessingLogCustomHeaderProperties {}; // Binla removed
        CustomHeaderProperties : Composition of many MessageProcessingLogCustomHeaderProperties
                                   on CustomHeaderProperties.Log = MessageGuid; // Binla added
        // MessageStoreEntries    : Association to many MessageStoreEntries {}; // Binla removed
        MessageStoreEntries    : Composition of many MessageStoreEntries
                                   on MessageStoreEntries.MessageGuid = MessageGuid; // Binla add
        ErrorInformation       : Association to MessageProcessingLogErrorInformations {};
        AdapterAttributes      : Association to many MessageProcessingLogAdapterAttributes {};
        Attachments            : Association to many MessageProcessingLogAttachments {};
        Runs                   : Association to many MessageProcessingLogRuns {};
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageProcessingLogAttachments {
    key Id          : LargeString not null;
        MessageGuid : LargeString;

        @odata.Type    : 'Edm.DateTime'
        TimeStamp   : DateTime;
        Name        : LargeString;
        ContentType : LargeString;
        PayloadSize : Integer64;

        @Core.MediaType: 'application/octet-stream'
        blob        : LargeBinary;
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity TraceMessageProperties {
    key TraceId : Integer64 not null;
    key Name    : LargeString not null;
        Value   : LargeString;
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageProcessingLogCustomHeaderProperties {
    key Id    : LargeString not null;
        Name  : LargeString;
        Value : LargeString;
        // Log   : Association to MessageProcessingLogs {}; // Binla removed
        Log   : LargeString; // Binla added
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageProcessingLogRunStepProperties {
    key RunId      : LargeString not null;
    key ChildCount : Integer not null;
    key Name       : LargeString not null;
        Value      : LargeString;
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageProcessingLogErrorInformations {
    key MessageGuid          : LargeString not null;
        Type                 : LargeString;
        LastErrorModelStepId : LargeString;

        @Core.MediaType: 'application/octet-stream'
        blob                 : LargeBinary;
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity MessageProcessingLogAdapterAttributes {
    key Id                   : LargeString not null;
        AdapterId            : LargeString;
        AdapterMessageId     : LargeString;
        Name                 : LargeString;
        Value                : LargeString;
        MessageProcessingLog : Association to MessageProcessingLogs {};
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity IdMapFromIds {
    key FromId : LargeString not null;
        ToIds  : Association to many IdMapToIds {};
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity IdMapFromId2s {
    key FromId         : LargeString not null;
        ToId2          : LargeString;
        Mapper         : LargeString;

        @odata.Type: 'Edm.DateTimeOffset'
        ExpirationTime : DateTime;
        ToId           : Association to IdMapToIds {};
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity IdMapToIds {
    key ToId           : LargeString not null;

        // @cds.java.name: 'FromIdValue'
        FromId_        : LargeString;
        Mapper         : LargeString;

        @odata.Type   : 'Edm.DateTimeOffset'
        ExpirationTime : DateTime;

        @cds.java.name: 'FromIdAssociation'
        FromId         : Association to IdMapFromIds {};
        FromId2s       : Association to many IdMapFromId2s {};
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity IdempotentRepositoryEntries {
    key HexSource      : LargeString not null;
    key HexEntry       : LargeString not null;
        Source         : LargeString;
        Entry          : LargeString;
        Component      : LargeString;
        CreationTime   : Integer64;
        ExpirationTime : Integer64;
  };

  @cds.external        : true
  @cds.persistence.skip: true
  entity GenericIdempotentRepositoryEntries {
    key HexVendor      : LargeString not null;
    key HexSource      : LargeString not null;
    key HexEntry       : LargeString not null;
    key HexComponent   : LargeString not null;
        Source         : LargeString;
        Entry          : LargeString;
        Component      : LargeString;
        Vendor         : LargeString;
        CreationTime   : Integer64;
        ExpirationTime : Integer64;
  };

  @cds.external: true
  type IntegrationArtifact {
    Id   : LargeString;
    Name : LargeString;
    Type : LargeString;
  };
};

// // Add these annotations to override the OData type
// annotate MessageProcessingLogs.MessageProcessingLogs with {
//   @odata.Type: 'Edm.DateTimeOffset'
//   LogStart;

//   @odata.Type: 'Edm.DateTimeOffset'
//   LogEnd;
// };

// // Keep your existing FilterRestrictions
// annotate MessageProcessingLogs.MessageProcessingLogs with @(Capabilities: {FilterRestrictions: {FilterExpressionRestrictions: [
//   {
//     Property          : 'LogStart',
//     AllowedExpressions: 'SingleValue'
//   },
//   {
//     Property          : 'LogEnd',
//     AllowedExpressions: 'SingleValue'
//   }
// ]}});
