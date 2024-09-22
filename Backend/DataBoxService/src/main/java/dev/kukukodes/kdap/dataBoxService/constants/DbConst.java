package dev.kukukodes.kdap.dataBoxService.constants;

public class DbConst {
    public static class Collections {
        public static final String DATA_BOX = "dataBox";
        public static final String DATA_ENTRIES = "dataEntries";
    }

    public static class DocumentFields {
        public static class CommonFields {
            public static final String ID = "_id";
            public static final String NAME = "name";
            public static final String CREATED = "created";
            public static final String MODIFIED = "modified";
            public static final String DESCRIPTION = "description";
        }

        public static class DataBox {
            public static final String USER_ID = "userId";
            public static final String FIELDS = "fields";

            public static class DataBoxField {
                public static final String FIELD_NAME = "fieldName";
                public static final String FIELD_TYPE = "fieldType";
                public static final String REQUIRED = "required";
            }
        }

        public static class DataEntry {
            public static final String STORE_ID = "storeID";
            public static final String VALUE = "values";

            public static class DataEntryValue {
                public static final String FIELD_NAME = "fieldName";
                public static final String VALUE = "value";
            }
        }
    }
}
