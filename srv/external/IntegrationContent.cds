/* checksum : b50e685e9954450814810d57a64183dc */
@cds.external : true
@m.IsDefaultEntityContainer : 'true'
service IntegrationContent {
  @cds.external : true
  @cds.persistence.skip : true
  entity IntegrationDesigntimeArtifacts {
    key Id : LargeString not null;
    key Version : LargeString not null;
    PackageId : LargeString;
    Name : LargeString;
    Description : LargeString;
    ArtifactContent : LargeBinary;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
    Configurations : Association to many Configurations {  };
    Resources : Association to many Resources {  };
    DesignGuidelineExecutionResults : Association to many DesignGuidelineExecutionResults {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity RuntimeArtifactErrorInformations {
    key Id : LargeString not null;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity IntegrationRuntimeArtifacts {
    key Id : LargeString not null;
    Version : LargeString;
    Name : LargeString;
    Type : LargeString;
    DeployedBy : LargeString;
    @odata.Type : 'Edm.DateTime'
    DeployedOn : DateTime;
    Status : LargeString;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
    ErrorInformation : Association to RuntimeArtifactErrorInformations {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity Configurations {
    key ParameterKey : LargeString not null;
    ParameterValue : LargeString;
    DataType : LargeString;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity Resources {
    key Name : LargeString not null;
    key ResourceType : LargeString not null;
    ReferencedResourceType : LargeString;
    ResourceContent : LargeBinary;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity IntegrationPackages {
    key Id : LargeString not null;
    Name : LargeString;
    Description : LargeString;
    ShortText : LargeString;
    Version : LargeString;
    Vendor : LargeString;
    Mode : LargeString;
    SupportedPlatform : LargeString;
    ModifiedBy : LargeString;
    CreationDate : LargeString;
    ModifiedDate : LargeString;
    CreatedBy : LargeString;
    Products : LargeString;
    Keywords : LargeString;
    Countries : LargeString;
    Industries : LargeString;
    LineOfBusiness : LargeString;
    IntegrationDesigntimeArtifacts : Association to many IntegrationDesigntimeArtifacts {  };
    ValueMappingDesigntimeArtifacts : Association to many ValueMappingDesigntimeArtifacts {  };
    MessageMappingDesigntimeArtifacts : Association to many MessageMappingDesigntimeArtifacts {  };
    CustomTags : Association to many CustomTags {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity ServiceEndpoints {
    key Id : LargeString not null;
    Name : LargeString;
    Title : LargeString not null;
    Version : LargeString not null;
    Summary : LargeString not null;
    Description : LargeString not null;
    @odata.Type : 'Edm.DateTime'
    LastUpdated : DateTime;
    Protocol : LargeString;
    EntryPoints : Association to many EntryPoints {  };
    ApiDefinitions : Association to many APIDefinitions {  };
    CustomTags : Association to many CustomTags {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity EntryPoints {
    key Url : LargeString not null;
    Name : LargeString not null;
    Type : LargeString;
    AdditionalInformation : LargeString;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity APIDefinitions {
    key Url : LargeString not null;
    Name : LargeString not null;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity ValueMappingDesigntimeArtifacts {
    key Id : LargeString not null;
    key Version : LargeString not null;
    PackageId : LargeString not null;
    Name : LargeString not null;
    Description : LargeString;
    ArtifactContent : LargeBinary;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
    ValMapSchema : Association to many ValMapSchema {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity ValMaps {
    key Id : LargeString not null;
    Value : Value not null;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity DefaultValMaps {
    key Id : LargeString not null;
    Value : Value not null;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity ValMapSchema {
    key SrcAgency : LargeString not null;
    key SrcId : LargeString not null;
    key TgtAgency : LargeString not null;
    key TgtId : LargeString not null;
    State : LargeString;
    ValMaps : Association to many ValMaps {  };
    DefaultValMaps : Association to many DefaultValMaps {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity IntegrationAdapterDesigntimeArtifacts {
    key Id : LargeString not null;
    Version : LargeString not null;
    PackageId : LargeString;
    Name : LargeString;
    ArtifactContent : LargeBinary;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity CustomTags {
    key Name : LargeString not null;
    Value : LargeString;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity CustomTagConfigurations {
    key Id : LargeString not null;
    CustomTagsConfigurationContent : LargeString;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity DataStores {
    key DataStoreName : LargeString not null;
    key IntegrationFlow : LargeString not null;
    key Type : LargeString not null;
    Visibility : LargeString;
    NumberOfMessages : Integer64;
    NumberOfOverdueMessages : Integer64;
    Entries : Association to many DataStoreEntries {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity DataStoreEntries {
    key Id : LargeString not null;
    key DataStoreName : LargeString not null;
    key IntegrationFlow : LargeString not null;
    key Type : LargeString not null;
    Status : LargeString;
    MessageId : LargeString;
    @odata.Type : 'Edm.DateTime'
    DueAt : DateTime;
    @odata.Type : 'Edm.DateTime'
    CreatedAt : DateTime;
    @odata.Type : 'Edm.DateTime'
    RetainUntil : DateTime;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity Variables {
    key VariableName : LargeString not null;
    key IntegrationFlow : LargeString not null;
    Visibility : LargeString;
    @odata.Type : 'Edm.DateTime'
    UpdatedAt : DateTime;
    @odata.Type : 'Edm.DateTime'
    RetainUntil : DateTime;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity MessageMappingDesigntimeArtifacts {
    key Id : LargeString not null;
    key Version : LargeString not null;
    PackageId : LargeString not null;
    Name : LargeString not null;
    Description : LargeString;
    ArtifactContent : LargeBinary;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
    Resources : Association to many Resources {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity IntegrationDesigntimeLocks {
    key ResourceId : LargeString not null;
    ArtifactId : LargeString not null;
    ArtifactName : LargeString;
    ArtifactType : LargeString;
    PackageId : LargeString;
    PackageName : LargeString;
    @odata.Type : 'Edm.DateTime'
    CreatedAt : DateTime;
    CreatedBy : LargeString;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity BuildAndDeployStatus {
    key TaskId : LargeString not null;
    Status : LargeString;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity MDIDeltaToken {
    key Operation : LargeString not null;
    key Entity : LargeString not null;
    key Version : LargeString not null;
    DeltaToken : LargeString;
    LastUpdateTimestamp : LargeString;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity ScriptCollectionDesigntimeArtifacts {
    key Id : LargeString not null;
    key Version : LargeString not null;
    PackageId : LargeString;
    Name : LargeString;
    Description : LargeString;
    ArtifactContent : LargeBinary;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
    Resources : Association to many Resources {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity DesignGuidelines {
    key GuidelineId : LargeString not null;
    GuidelineName : LargeString;
    Category : LargeString;
    Severity : LargeString;
    Applicability : LargeString;
    Compliance : LargeString;
    IsGuidelineSkipped : Boolean;
    SkipReason : LargeString;
    SkippedBy : LargeString;
    ExpectedKPI : LargeString;
    ActualKPI : LargeString;
    ViolatedComponents : LargeString;
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity DesignGuidelineExecutionResults {
    key ExecutionId : LargeString not null;
    ArtifactVersion : LargeString;
    ExecutionStatus : LargeString;
    ExecutionTime : LargeString;
    ReportType : LargeString;
    @Core.MediaType : 'application/octet-stream'
    blob : LargeBinary;
    DesignGuidelines : Association to many DesignGuidelines {  };
  };

  @cds.external : true
  @cds.persistence.skip : true
  entity RuntimeArtifact {
    key id : LargeString not null;
    name : LargeString;
    symbolicName : LargeString;
    type : LargeString;
    version : LargeString;
    nodeType : LargeString;
    deployedBy : LargeString;
    @odata.Type : 'Edm.DateTime'
    deployedOn : DateTime;
    deployState : LargeString;
  };

  @cds.external : true
  type IntegrationArtifact {
    Id : LargeString;
    Name : LargeString;
    Type : LargeString;
  };

  @cds.external : true
  type Scheduler {
    TriggerType : LargeString;
    DateType : LargeString;
    TimeType : LargeString;
    Date : LargeString;
    Time : LargeString;
    DayOfMonth : LargeString;
    Monday : Boolean;
    Tuesday : Boolean;
    Wednesday : Boolean;
    Thursday : Boolean;
    Friday : Boolean;
    Saturday : Boolean;
    Sunday : Boolean;
    OnEveryDuration : Integer;
    FromInterval : LargeString;
    ToInterval : LargeString;
    TimeZone : LargeString;
  };

  @cds.external : true
  type Value {
    SrcValue : LargeString;
    TgtValue : LargeString;
  };

  @cds.external : true
  action DeployIntegrationDesigntimeArtifact(
    Id : LargeString not null,
    Version : LargeString not null
  ) returns IntegrationDesigntimeArtifacts;

  @cds.external : true
  action IntegrationDesigntimeArtifactSaveAsVersion(
    Id : LargeString not null,
    SaveAsVersion : LargeString not null
  ) returns IntegrationDesigntimeArtifacts;

  @cds.external : true
  action CopyIntegrationPackage(
    Id : LargeString not null,
    ImportMode : LargeString,
    Suffix : LargeString
  ) returns IntegrationPackages;

  @cds.external : true
  action DeployValueMappingDesigntimeArtifact(
    Id : LargeString not null,
    Version : LargeString not null
  ) returns ValueMappingDesigntimeArtifacts;

  @cds.external : true
  action ValueMappingDesigntimeArtifactSaveAsVersion(
    Id : LargeString not null,
    SaveAsVersion : LargeString not null
  ) returns ValueMappingDesigntimeArtifacts;

  @cds.external : true
  action UpsertValMaps(
    Id : LargeString not null,
    Version : LargeString not null,
    SrcAgency : LargeString not null,
    SrcId : LargeString not null,
    TgtAgency : LargeString not null,
    TgtId : LargeString not null,
    ValMapId : LargeString,
    SrcValue : LargeString not null,
    TgtValue : LargeString not null,
    IsConfigured : Boolean not null
  ) returns ValMaps;

  @cds.external : true
  action DeleteValMaps(
    Id : LargeString not null,
    Version : LargeString not null,
    SrcAgency : LargeString not null,
    SrcId : LargeString not null,
    TgtAgency : LargeString not null,
    TgtId : LargeString not null
  ) returns LargeString;

  @cds.external : true
  action UpdateDefaultValMap(
    Id : LargeString not null,
    Version : LargeString not null,
    SrcAgency : LargeString not null,
    SrcId : LargeString not null,
    TgtAgency : LargeString not null,
    TgtId : LargeString not null,
    ValMapId : LargeString not null,
    IsConfigured : Boolean not null
  ) returns DefaultValMaps;

  @cds.external : true
  action DeployIntegrationAdapterDesigntimeArtifact(
    Id : LargeString not null
  ) returns IntegrationAdapterDesigntimeArtifacts;

  @cds.external : true
  action DeployMessageMappingDesigntimeArtifact(
    Id : LargeString not null,
    Version : LargeString not null
  ) returns MessageMappingDesigntimeArtifacts;

  @cds.external : true
  action MessageMappingDesigntimeArtifactSaveAsVersion(
    Id : LargeString not null,
    SaveAsVersion : LargeString not null
  ) returns MessageMappingDesigntimeArtifacts;

  @cds.external : true
  action DeployScriptCollectionDesigntimeArtifact(
    Id : LargeString not null,
    Version : LargeString not null
  ) returns ScriptCollectionDesigntimeArtifacts;
};

