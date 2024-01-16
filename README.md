## Mapping Scenarios

| LHS (Source - MVisitable) | RHS (Destination - Mappable)                                    |
|---------------------------|-----------------------------------------------------------------|
| basic type                | basic type with <br/> same name <br/> same basic type           |
| basic type                | basic type with <br/> same name <br/> different basic type      |
| basic type                | basic type with <br/> different name <br/> same basic type      |
| basic type                | basic type with <br/> different name <br/> different basic type |
| basic type                | property inside object type                                     |
| collection type           | collection type with same name                                  |
| collection type           | collection type with different name                             |

### Important observations

- When a different basic type is present, there needs to be a converter supplied
- 