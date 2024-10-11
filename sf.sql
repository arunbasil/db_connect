SELECT
    src.RECORD_CONTENT:data.accountName::VARCHAR AS accountName,
    src.RECORD_CONTENT:data.accountNumber::VARCHAR AS accountNumber,
    src.RECORD_CONTENT:data.accountType::VARCHAR AS accountType,
    src.RECORD_CONTENT:data.bnzRequestId::VARCHAR AS RequestId,
    src.RECORD_CONTENT:data.correlationId::VARCHAR AS correlationId,
    src.RECORD_CONTENT:data.customerNumber::VARCHAR AS customerNumber,
    src.RECORD_CONTENT:data.eventTimestamp::VARCHAR AS eventTimestamp,
    src.RECORD_CONTENT:data.schemaName::VARCHAR AS schemaName,
    src.RECORD_CONTENT:data.sourceId::VARCHAR AS sourceId,
    src.RECORD_CONTENT:metadata.id::VARCHAR AS metadataId,
    src.RECORD_CONTENT:metadata.source::VARCHAR AS metadataSource,
    src.RECORD_CONTENT:metadata.subject::VARCHAR AS metadataSubject,
    src.RECORD_CONTENT:metadata.time::VARCHAR AS metadataTime,
    src.RECORD_CONTENT:metadata.type::VARCHAR AS metadataType
FROM 
    NAME_VERIFICATION_REQ_LOG_DEV_FLATTEN src,
    LATERAL FLATTEN(input => src.RECORD_CONTENT:data) data_flat
