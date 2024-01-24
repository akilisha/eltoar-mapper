## Lightweight object properties mapper

Fast and lightweight mapper for copying properties between data objects. The library does not require additional
implementation of interfaces or extending base classes to be able to take advantage of, and most importantly, it avoids
relying on the reflection API.

## Mapping Scenarios

In typed programming languages, there are some two pieces of information that are of paramount significance when
examining any piece of data.

1. the __name__ associated to a data property (the name of a field in an object)
2. the __data type__ associated with a data property (the size of data which the property can hold)

Now, when copying the value of one property to another property, it is necessary to have a mapping that will associate
the source data property (__herein called lhs__) to the corresponding destination data property (__herein called rhs__).
Without such a mapping, it would be impossible to know what data value from the lsh should be applied to what property
in the rhs.

Although not always visible, this mapping information is unsurprisingly always available. It can be quickly deduced in
one of two ways, or a combination of both:

1. implicitly - no mapping information is explicitly provided. In this case, all the metadata that can be inferred
   about the *lhs* property __should be assumed__ to apply in exactly the same way to the *rhs* property.
2. explicitly - mapping information is provided, and it is well-defined for each property, in terms of
    - name of the field
    - data type for the field
    - multiplicity of the field

One of the best ways to understand data mapping scenarios is to first begin with understanding what mismatches you have
to deal with. This is the approach taken by this library, and the methods available in the mapper are the best way to
tell this story.

### One to one mapping with no mismatch

```bash
LRMapping.init().merge(src, dest)
```

This is a best-case-scenario where data is being copied from one object of type _"A"_ to another instance of the
same type _"B"_. In this use-case, it would be like copying an instance of __Tenant__ to another instance of __Tenant__.

```java
public class Rental {
    Long id;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zipCode;
}

public class Tenant {
    Long id;
    String firstName;
    String lastName;
    boolean accepted;
    short entryCode;
    Rental location;
}
```

Copying the data here will require no explicit mapping:

```bash
Tenant src = new Tenant(...initialize all the tenant data...);
Tenant dest = new Tenant();
LRMapping.init().merge(src, dest);
```

And just like that, the __dest__ will have values identical to those in __src__.

### Property name mismatch

```map(String src, String dest)```

In this scenario, everything else about a property remains constance except the name not being exactly matched.

```java
public class Tenant_A {
    Long id;
    String first_name;
    String last_name;
    boolean accepted;
    short entry_code;
    Rental location;
}
```

Copying the data here will require mapping the mismatched field names, from __left__ (source) to __right__ (
destination):

```bash
Tenant src = new Tenant(...initialize all the tenant data...);
Tenant_A dest = new Tenant_A();
LRMapping.init()
        .map("firstName", "first_name")
        .map("lastName", "last_name")
        .map("entryCode", "entry_code")
        .merge(src, dest);
```

Now, you probably may be asking, "what if the mismatch is in the nested __Rental__ object"? Ok, be a bit more patient.
We'll get to that soon.

### Property type mismatch

```map(String src, Function<Object, R> eval)```

In this scenario, everything else about a property remains constance except the data type not being exactly matched.

```java
import java.math.BigDecimal;

public class Tenant_B {
    BigDecimal id;
    String firstName;
    String lastName;
    boolean accepted;
    String entryCode;
    Rental location;
}
```

In this scenario, the __id__ and __entryCode__ properties have different data types, and therefore an additional
converter function is necessary to handle the mismatch

```bash
Tenant src = new Tenant(...initialize all the tenant data...);
Tenant_B dest = new Tenant_B();
LRMapping.init()
        .map("id", (id) -> BigDecimal.valueOf((long)id))
        .map("entryCode", (code) -> ((Short)code).toString())
        .merge(src, dest);
```

### Mismatch on both property name and type

```map(String src, String dest, Function<Object, R> eval)```

In this scenario, both the property name and type as mismatched, but the multiplicity remains the same

```java
public class Tenant_C {
    BigDecimal guestNumber;
    String guestName;
    boolean accepted;
    String guestCode;
    Rental location;
}
```

In this scenario, the __id__ and __entryCode__ properties in __Tenant__ need to be matched to __guestNumber__ and
__guestCode__ respectively in __Tenant_C__. The properties have different data types as well, and therefore an
additional converter function is necessary to handle the mismatch as well.

```bash
Tenant src = new Tenant(...initialize all the tenant data...);
Tenant_B dest = new Tenant_B();
LRMapping.init()
        .map("id", "guestNumber", (id) -> BigDecimal.valueOf((long)id))
        .map("firstName", "guestName")
        .map("entryCode", "guestCode", (code) -> ((Short)code).toString())
        .merge(src, dest);
```

In __Tenant_C__, there is no __firstName__ or __lastName__, but instead, it has a __guestName__. This property is mapped
to the __firstName__ in __Tenant__.

### Property multiplicity mismatch - embedded rhs

```map(String src, String dest, Class<?> collectionType)```

In this scenario, one or more properties of the __lhs__ are matched to properties in a __rhs__ object through either:

1. an embedded object
2. a single element in a list

The best way to visualize this use-case is considering relationships from a database point of view

- An embedded object may represent:
    - multiple columns in one table being mapped to an embedded data object
- A single element list may represent:
    - a 1-to-1 mapping where the object represented through the foreign key is mapped to a single collection element

For illustration, assume an object __Tenant_D__ which is similar to __Tenant__, but with the __Rental__ flattened out.

```java
public class Tenant_D {
    Long id;
    String firstName;
    String lastName;
    boolean accepted;
    String code;
    Long unitId;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zip;
}
```

Now assume that the goal is to map __Tenant_D__ into __Tenant__.

```bash
Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
Tenant dest = new Tenant();
LRMapping.init()
        .map("code", "entryCode", str -> Short.parseShort((String) str))
        .map("unitId", "location.id")
        .map("unitNumber", "location.unitNumber")
        .map("streetName", "location.streetName")
        .map("city", "location.city")
        .map("state", "location.state")
        .map("zip", "location.zipCode")
        .merge(src, dest);
```

It's easy to observe that multiple entries on the __lhs__ map to a common node (__location__) on the __rhs__. This node
is the __location__ property in __Tenant__. I'm sure you may be asking, "why repeat the property names in __Rental__
if they match corresponding properties on the __lhs__?" Well, you're in luck because this repetition is not necessary.
So the mapping below will __also__ work, where the __lsh__ column names are mapped to the common node followed by a
__mandatory__ '.' (period) character, and as a result, __rhs__ fields names will become implied.

```bash
LRMapping.init()
        .map("code", "entryCode", str -> Short.parseShort((String) str))
        .map("unitId", "location.id")
        .map("unitNumber", "location.")
        .map("streetName", "location.")
        .map("city", "location.")
        .map("state", "location.")
        .map("zip", "location.zipCode")
        .merge(src, dest);
```

Taking this example a step further, what would happen when the levels of nesting increase? Take __Tenant_E__ who has a
__NameInfo__ object and a __CityInfo__ object inside __Rental_E__.

```java
public class NameInfo {
    String firstName;
    String lastName;
}

public class CityInfo {
    String city;
    String state;
    String zipCode;
}

public class Rental_E {
    Long id;
    String unitNumber;
    String streetName;
    CityInfo cityInfo;
}

public class Tenant_E {
    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    Rental_E location;
}
```

Can __Tenant_E__ be mapped from __Tenant_D__?

```bash
Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
Tenant_E dest = new Tenant_E();
LRMapping.init()
        .map("firstName", "name.")
        .map("lastName", "name.")
        .map("code", "entryCode", str -> Short.parseShort((String) str))
        .map("unitId", "location.id")
        .map("unitNumber", "location.")
        .map("streetName", "location.")
        .map("city", "location.cityInfo.")
        .map("state", "location.cityInfo.")
        .map("zip", "location.cityInfo.zipCode")
        .merge(src, dest);
```

And yes, it's ok to keep nested __rightwards__ - as long as there are corresponding nested entities in the model objects
then it's all good.

Now, what about the other representation making use of a Collection type? This happens when you model the __rhs__ to
have a __collection element type__ field which will contain the related domain model. The key thing to remember is that
this __collection__ can __ONLY__ have a single item inside.

To illustrate this, create __Tenant_F__ from copying __Tenant_E__ and only change the "__location__" field to be a of
__Set__ type.

```java
public class Tenant_F {
    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    Set<Rental> locations;
}
```

To copy data from __Tenant_D__ into __Tenant_F__, this will require adding __collection element type__ information in
the mapping

```bash
Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
Tenant_F dest = new Tenant_F();
LRMapping.init()
        .map("firstName", "name.")
        .map("lastName", "name.")
        .map("code", "entryCode", str -> Short.parseShort((String) str))
        .map("unitId", "locations.id", Rental.class)
        .map("unitNumber", "locations.", Rental.class)
        .map("streetName", "locations.", Rental.class)
        .map("city", "locations.", Rental.class)
        .map("state", "locations.", Rental.class)
        .map("zip", "locations.zipCode", Rental.class)
        .merge(src, dest);
```

As hoped for, the above mapping works effortlessly. "But what the __locations__ field in __Tenant_F__ was of type
__Set<Rental_E>__ instead?" Well, this should not present a problem either. The mapping would simply have to reflect
the underlying relations between the model objects. To illustrate this, create a __Tenant_G__ object to match this
requirement

```java
public class Tenant_G {
    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    Set<Rental_E> locations;
}
```

The corresponding matching would be something already very familiar.

```bash
Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
Tenant_G dest = new Tenant_G();
LRMapping.init()
        .map("firstName", "name.")
        .map("lastName", "name.")
        .map("code", "entryCode", str -> Short.parseShort((String) str))
        .map("unitId", "locations.id", Rental_E.class)
        .map("unitNumber", "locations.", Rental_E.class)
        .map("streetName", "locations.", Rental_E.class)
        .map("city", "locations.cityInfo.", Rental_E.class)
        .map("state", "locations.cityInfo.", Rental_E.class)
        .map("zip", "locations.cityInfo.zipCode", Rental_E.class)
        .merge(src, dest);
```

The mapping just illustrated combines aspects of both _single collection element_ and the same entity having
__nested elements__ in itself. This leaves the door wide open for creating mappings for a lot more complex combinations.

And now for good measure, what if the __Set<Rental_E>__ property in __Tenant_G__ above was replaced by an __array__
object instead? For illustration, look at __Tenant_K_ below.

```java
public class Tenant_K {
    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    Rental[] locations;
}
```

How would the mapping be affected in this case?

```bash
Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
Tenant_K dest = new Tenant_K();
LRMapping.init()
        .map("firstName", "name.")
        .map("lastName", "name.")
        .map("code", "entryCode", str -> Short.parseShort((String) str))
        .map("unitId", "locations.id", Rental.class)
        .map("unitNumber", "locations.", Rental.class)
        .map("streetName", "locations.", Rental.class)
        .map("city", "locations.", Rental.class)
        .map("state", "locations.", Rental.class)
        .map("zip", "locations.zipCode", Rental.class)
        .merge(src, dest);
```

And the actual mapping works without any alteration. So the treatment of a single element array is handled
transparently just like a single element collection, which eliminates the need to reason differently about two similar
concepts.

### Property multiplicity mismatch - embedded rhs with converted function

```map(String src, String dest, Class<?> collectionItemType, Function<Object, R> eval)```

To extend the previous illustrations even further, it's possible to apply a __converter function__ to a property on the
__rhs__.

```java
public class Rental_F {
    String id;
    String unitNumber;
    String streetName;
    CityInfo cityInfo;
}

public class Tenant_H {
    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    Set<Rental_F> locations;
}
```

The example above is using a __String__ type in __id__ in the __Rental_F__ object. The mapping in the previous example
will remain the same except for the addition of a converter function

```bash
Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
Tenant_H dest = new Tenant_H();
LRMapping.init()
        .map("firstName", "name.")
        .map("lastName", "name.")
        .map("code", "entryCode", str -> Short.parseShort((String) str))
        .map("unitId", "locations.id", Rental_F.class, String::valueOf)
        .map("unitNumber", "locations.", Rental_F.class)
        .map("streetName", "locations.", Rental_F.class)
        .map("city", "locations.cityInfo.", Rental_F.class)
        .map("state", "locations.cityInfo.", Rental_F.class)
        .map("zip", "locations.cityInfo.zipCode", Rental_F.class)
        .merge(src, dest);
```

### Property multiplicity mismatch - embedded lhs

```map(String src, Class<?> collectionType, String dest)```

In some situations, the __embedded entity__ or the __single collection entity__ might be the source of data, which needs
to be mapped to a flattened model. This is the reverse of the illustrations from the section above.

The illustration below shows how to map __Tenant_I__ into __Tenant_D__. The __rhs__ has one field (__location__) of type
__Rental_D__ whose properties need to __spread out__ into the matching properties in __Tenant_D__.

```java
public class Tenant_I {
    Long id;
    String firstName;
    String lastName;
    boolean accepted;
    short entryCode;
    Rental_D location;
}

public class Rental_D {
    Long unitId;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zip;
}

public class Tenant_D {
    Long id;
    String firstName;
    String lastName;
    boolean accepted;
    String code;
    Long unitId;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zip;
}
```

The absolutely important thing to bear in mind here is that for an embedded property on the __lhs__, it cannot have a
destination mapping in the __rhs__. For example, for the source property __location__ which exists in __Tenant_I__,
there cannot be a destination mapping for the same in __Tenant_D__. The properties of __Rental_D__ already exist in
__Tenant_D__.

If all the properties of the __Lhs__ entity exactly match the target properties in the __rhs__, the the mapping below
should be sufficient. The details of the __rhs__ properties are inferred from the properties on the __lhs__.

```bash
Tenant_I src = new Tenant_I(123L, "first", "customer", true, (short)123, new Rental_D(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));
Tenant_D dest = new Tenant_D();
LRMapping.init()
        .map("entryCode", "code", sh -> Short.toString((Short) sh))
        .map("location")
        .merge(src, dest);
```

You may also be able to optionally add a mapping configuration if any property requires customized mapping. Consider the
example of mapping __Tenant__ into __Tenant_D__.

```java
public class Tenant {
    Long id;
    String firstName;
    String lastName;
    boolean accepted;
    short entryCode;
    Rental location;
}

public class Rental {
    Long id;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zipCode;
}
```

The necessary configuration will need a custom mapping entry when specific mapping of nested fields is necessary.

```bash
Tenant src = new Tenant(123L, "first", "customer", true, (short) 123, new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));
Tenant_D dest = new Tenant_D();
LRMapping.init()
        .map("entryCode", "code", sh -> Short.toString((Short) sh))
        .map("location", LRMapping.init()
                .map("id", "unitId")
                .map("zipCode", "zip"))
        .merge(src, dest);
```

### Property multiplicity mismatch - single collection element on lhs

```map(String src, Class<?> collectionType, LRMapping nestedMapping)```

In a manner similar to the previously illustrated __rhs__ mapping, a __single collection element__ can also be placed
on the __lhs__ and configured to map into properties on the __rhs__.

Consider the case of mapping __Tenant_J__ into __Tenant_D__.

> Notice that __Tenant_J__ is using a __List__ for the collection type, and not a __Set__. That's because the __Set__
> interface does not provide a __get(index)__ function which can access elements through their index. For this reason,
> when working with this particular use case scenario, stick to using a collection type that can be indexed, such as a
__List__ or an __array__

```java
public class Tenant_J {
    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    List<Rental> locations;
}

public class Rental {
    Long id;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zipCode;
}
```

This time, instead of __Rental__ being embedded, it's now a single element inside a collection. Unsurprisingly, the
necessary mapping is also something already familiar

```bash
Tenant src = new Tenant(123L, "first", "customer", true, (short) 123, new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));
Tenant_J src = new Tenant_J(123L, new NameInfo("first", "customer"), true, (short) 123, List.of(new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789")));
Tenant_D dest = new Tenant_D();
LRMapping.init()
        .map("name", LRMapping.init()
                .map("first", "firstName")
                .map("last", "lastName"))
        .map("entryCode", "code", sh -> Short.toString((Short) sh))
        .map("locations", Rental.class, LRMapping.init()
                .map("id", "unitId")
                .map("zipCode", "zip"))
        .merge(src, dest);
```

And speaking of an index-able collection, what about using a __Rental[]__ array?

```java
public class Tenant_K {
    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    Rental[] locations;
}

public class Rental {
    Long id;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zipCode;
}
```

As one would expect, the mapping does not change. A __List__ element or an __array__ are both given the same treatment
as far as retrieving the __lhs__ properties is concerned.

```bash
Tenant_K src = new Tenant_K(123L, new NameInfo("first", "customer"), true, (short) 123, new Rental[]{new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789")});
Tenant_D dest = new Tenant_D();
LRMapping.init()
        .map("name", LRMapping.init()
                .map("first", "firstName")
                .map("last", "lastName"))
        .map("entryCode", "code", sh -> Short.toString((Short) sh))
        .map("locations", Rental.class, LRMapping.init()
                .map("id", "unitId")
                .map("zipCode", "zip"))
        .merge(src, dest);
```

The depth of resolving properties from the __lhs__ has so far only been a single level. This is a design choice and not
a missed use case. The cognitive overhead of reasoning about properties as the level of indentation grows deeper on the
__lhs__ increases significantly. So it's more practical to think of the __lhs__ as a __flat point of reference__ from
where properties on the __rhs__ can be resolved. The level of indentation on the __lhs__ is therefore capped at

1. 1 nested element, or
2. 1 array or collection element

### Property multiplicity mismatch - collection elements on both lhs and rhs

```map(String src, Class<?> srcCollectionType, String dest, Class<?> destCollectionType, LRMapping collectionTypeMapping)```

Inevitably, throughout all the previous discussions, this scenario was eventually going to pop up, and so here we are.

This use case scenario is applied when mapping a collection property on the __lhs__ to a corresponding property on the
__rhs__, that it's. Pretty straightforward. Consider a collection of __Tenant_K__ objects being mapped to a collection
of __Tenant_D__. This particular example provides a very elaborate illustration of this scenario.

```java
public class RecordsA {
    String name;
    Set<Tenant_K> tenants;
}

public class RecordsB {
    String title;
    Tenant_D[] tenants;
}
```

For convenience, both __Tenant_K__ and __Tenant_D__ are exactly the same as the previous illustration. So how will
mapping those two different collections be accomplished?

```bash
Tenant_K k1 = new Tenant_K(123L, new NameInfo("muchina", "gachanja"), true, (short) 123, new Rental[]{new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789")});
Tenant_K k2 = new Tenant_K(456L, new NameInfo("ondieki", "mong'are"), false, (short) 987, new Rental[]{new Rental(567L, "2002", "superior ave", "dearborn", "MI", "56778")});
RecordsA src = new RecordsA("records from server A", Set.of(k1, k2));
RecordsC dest = new RecordsC();
LRMapping.init()
        .map("name", "title")
        .map("tenants", Tenant_K.class, "tenants", Tenant_D.class,
                LRMapping.init()
                        .map("name", LRMapping.init()
                                .map("first", "firstName")
                                .map("last", "lastName"))
                        .map("entryCode", "code", sh -> Short.toString((Short) sh))
                        .map("locations", Rental.class, LRMapping.init()
                                .map("id", "unitId")
                                .map("zipCode", "zip")))
        .merge(src, dest);
```

### Property multiplicity mismatch - collection elements on lhs and map on rhs

```LRMapping map(String src, Class<?> srcCollectionType, String dest, Class<?> destCollectionType, String keyField, LRMapping collectionTypeMapping)```

The first thing to note when working with a __Map__ property, is that the value of the __Map's__ key attribute can only
work with a few __SPECIFIC__ types:

1. __String__ - this is to remain aligned with JSON conventions
2. __Java Boxed Primitives__ - this is an added capability for the convenience of library users. Remember that this is
   __NOT__ a valid JSON property.

With that out of the way, you can extend the previous example to use a map field.

```java
public class RecordsA {
    String name;
    Set<Tenant_K> tenants;
}

public class RecordsC {
    String title;
    Map<Long, Tenant_D> tenants;
}
```

So how does this affect the mapping? Well, the mapping is actually identical with the previous illustration with one
small tweak - provide the name of a property in the __source__ object whose value will be the map's key value.

The only line that would change is this one:

```.map("tenants", Tenant_K.class, "tenants", Tenant_D.class, "id",```

And the full mapping is shown below

```bash
Tenant_K k1 = new Tenant_K(123L, new NameInfo("muchina", "gachanja"), true, (short) 123, new Rental[]{new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789")});
Tenant_K k2 = new Tenant_K(456L, new NameInfo("ondieki", "mong'are"), false, (short) 987, new Rental[]{new Rental(567L, "2002", "superior ave", "dearborn", "MI", "56778")});
RecordsA src = new RecordsA("records from server A", Set.of(k1, k2));
RecordsC dest = new RecordsC();
LRMapping.init()
        .map("name", "title")
        .map("tenants", Tenant_K.class, "tenants", Tenant_D.class, "id",
                LRMapping.init()
                        .map("name", LRMapping.init()
                                .map("first", "firstName")
                                .map("last", "lastName"))
                        .map("entryCode", "code", sh -> Short.toString((Short) sh))
                        .map("locations", Rental.class, LRMapping.init()
                                .map("id", "unitId")
                                .map("zipCode", "zip")))
        .merge(src, dest);
```

Now what if the request was reversed, and the task was therefore to copy __RecordsC__ into __RecordsA__? The actual
mapping happening beneath the hood would be from the Map's __values()__ (lhs) to the destination collection values (
rhs).
The new configuration would therefore now look like this.

```bash
RecordsA newDest = new RecordsA();

LRMapping.init()
        .map("title", "name")
        .map("tenants", Tenant_D.class, "tenants", Tenant_K.class, LRMapping.init()
                .map("firstName", "name.firstName")
                .map("lastName", "name.lastName")
                .map("code", "entryCode", str -> Short.parseShort((String) str))
                .map("unitId", "locations.id", Rental.class)
                .map("unitNumber", "locations.", Rental.class)
                .map("streetName", "locations.", Rental.class)
                .map("city", "locations.", Rental.class)
                .map("state", "locations.", Rental.class)
                .map("zip", "locations.zipCode", Rental.class))

        .merge(dest, newDest);
```

