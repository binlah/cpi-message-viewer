/* checksum : aa69e3ad21575cbe962bd12c7df7fbb5 */
@cds.external : true
@m.IsDefaultEntityContainer : 'true'
service MessageStore {
  @cds.external : true
  @cds.persistence.skip : true
  entity MessageStoreEntries {
    key Id : LargeString not null;
    MessageGuid : LargeString;
    MessageStoreId : LargeString;
    @odata.Type : 'Edm.DateTime'
    TimeStamp : DateTime;
    HasAttachments : Boolean;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
    Attachments : Association to many MessageStoreEntryAttachments {  };
    Properties : Association to many MessageStoreEntryProperties {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity MessageStoreEntryAttachmentProperties {
    key AttachmentId : LargeString not null;
    key Name : LargeString not null;
    Value : LargeString;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity MessageStoreEntryAttachments {
    key Id : LargeString not null;
    Name : LargeString;
    ContentType : LargeString;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
    Properties : Association to many MessageStoreEntryAttachmentProperties {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity MessageStoreEntryProperties {
    key MessageId : LargeString not null;
    key Name : LargeString not null;
    Value : LargeString;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity MessageProcessingLogs {
    key MessageGuid : LargeString not null;
    CorrelationId : LargeString;
    ApplicationMessageId : LargeString;
    ApplicationMessageType : LargeString;
    @odata.Type : 'Edm.DateTime'
    LogStart : DateTime;
    @odata.Type : 'Edm.DateTime'
    LogEnd : DateTime;
    Sender : LargeString;
    Receiver : LargeString;
    IntegrationFlowName : LargeString;
    Status : LargeString;
    AlternateWebLink : LargeString;
    IntegrationArtifact : IntegrationArtifact not null;
    LogLevel : LargeString;
    CustomStatus : LargeString;
    MessageStoreEntries : Association to many MessageStoreEntries {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity JmsBrokers {
    key ![Key] : LargeString not null;
    Capacity : Integer64;
    MaxCapacity : Integer64;
    IsTransactedSessionsHigh : Integer;
    IsConsumersHigh : Integer;
    IsProducersHigh : Integer;
    MaxQueueNumber : Integer64;
    QueueNumber : Integer64;
    CapacityOk : Integer64;
    CapacityWarning : Integer64;
    CapacityError : Integer64;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity NumberRanges {
    key Name : LargeString not null;
    Description : LargeString;
    MaxValue : LargeString;
    MinValue : LargeString;
    Rotate : LargeString;
    CurrentValue : LargeString;
    FieldLength : LargeString;
    DeployedBy : LargeString;
    @odata.Type : 'Edm.DateTime'
    DeployedOn : DateTime;
  };

  @cds.external : true
  type IntegrationArtifact {
    Id : LargeString;
    Name : LargeString;
    Type : LargeString;
  };
};

