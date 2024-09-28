# Kuku Data Auto Pilot (KDAP) Actions Architecture


## Basic Scenario and Core Actions

Let's consider a basic scenario where a user wants to manage a customer database. They need to perform operations like adding new customers, updating customer information, and filtering customers based on certain criteria.

### Core Actions

1. **Create DataBox**
    - Inputs:
        - name: Text (required)
        - description: Text (optional)
    - Outputs:
        - newDataBox: DataBox

2. **Add Field to DataBox**
    - Inputs:
        - targetDataBox: DataBox (required)
        - fieldName: Text (required)
        - fieldType: Text (required, allowed values: "Text", "Integer", "Float", "Date", "Boolean")
    - Outputs:
        - updatedDataBox: DataBox

3. **Add DataEntry to DataBox**
    - Inputs:
        - targetDataBox: DataBox (required)
        - fieldValues: List of FieldValue (required)
    - Outputs:
        - newDataEntry: DataEntry
        - updatedDataBox: DataBox

4. **Update DataEntry**
    - Inputs:
        - targetDataBox: DataBox (required)
        - targetDataEntry: DataEntry (required)
        - updatedFieldValues: List of FieldValue (required)
    - Outputs:
        - updatedDataEntry: DataEntry
        - updatedDataBox: DataBox

5. **Delete DataEntry**
    - Inputs:
        - targetDataBox: DataBox (required)
        - targetDataEntry: DataEntry (required)
    - Outputs:
        - updatedDataBox: DataBox

6. **Filter DataBox**
    - Inputs:
        - sourceDataBox: DataBox (required)
        - filterField: Field (required)
        - filterValue: Text (required)
        - filterOperator: Text (required, allowed values: "equals", "contains", "greater_than", "less_than")
    - Outputs:
        - filteredDataBox: DataBox

7. **Sort DataBox**
    - Inputs:
        - sourceDataBox: DataBox (required)
        - sortField: Field (required)
        - sortOrder: Text (required, allowed values: "ascending", "descending")
    - Outputs:
        - sortedDataBox: DataBox

### Example Workflow

Here's how these core actions could be used in a basic workflow:

1. Create a "Customers" DataBox
2. Add fields to the "Customers" DataBox:
    - Name (Text)
    - Email (Text)
    - Age (Integer)
    - JoinDate (Date)
3. Add a new customer entry
4. Update a customer's information
5. Filter customers by age
6. Sort customers by join date

This basic set of actions provides the essential CRUD (Create, Read, Update, Delete) operations for managing data in KDAP, along with some basic data manipulation actions like filtering and sorting.

### Input/Output Types


1. **Primitive Types**
    - Text
    - Integer
    - Float
    - Date
    - Boolean

2. **KDAP-specific Types**
    - DataBox
    - DataEntry
    - Field
    - FieldValue
    - List of FieldValue

