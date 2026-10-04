# Online Store Order Management

Spring ORM + JPA/Hibernate, plain Spring (no Boot), H2 in-memory.

This README covers the six items the brief asks for in §5 (relationship
mappings, owning sides, fetch choices, cascade choices, inheritance strategy,
query decisions) plus the §8 explanation of why `applyDiscount` clears the
persistence context.

---

## 1. Relationship mappings

| Association | Kind | Where the FK lives |
|---|---|---|
| `Customer` ↔ `Order` | bidirectional `@OneToMany` / `@ManyToOne` | `orders.customer_id` |
| `Order` ↔ `OrderItem` | bidirectional `@OneToMany` / `@ManyToOne` | `order_item.order_id` |
| `OrderItem` → `Product` | unidirectional `@ManyToOne` | `order_item.product_id` |
| `Order` ↔ `Payment` | bidirectional `@OneToOne` | `payment.order_id` (unique) |
| `Product` ↔ `Category` | bidirectional `@ManyToMany` | join table `product_category(product_id, category_id)` |
| `Customer` value object | `@Embedded Address` (`@Embeddable`) | flattened into `customer.street/city/country` |
| `BaseEntity` shared fields | `@MappedSuperclass` | `id`, `created_at`, `version` on every table |
| `AuditLog` | own `@Entity` | `audit_log` table |

---

## 2. Owning sides

The owning side is the one with the `@JoinColumn` / `@JoinTable`; the inverse side uses `mappedBy` and holds no column. Bidirectional associations are kept in sync with helper methods.

- **`Order.customer`** owns `Customer ↔ Order`. `@ManyToOne` with `@JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_customer"))`. Inverse side `Customer.orders` is `@OneToMany(mappedBy = "customer")` and is kept in sync by `Customer.addOrder(Order)`, which the `Order(Customer)` constructor calls.
- **`OrderItem.order`** owns `Order ↔ OrderItem`. `@ManyToOne` with `@JoinColumn(name = "order_id", nullable = false)`. Inverse side `Order.items` is `@OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)`. Sync helpers `Order.addItem(Product, int)` (creates the `OrderItem` with the current price) and `Order.removeItem(OrderItem)` (nulls the back-reference).
- **`OrderItem.product`** owns `OrderItem → Product`. Unidirectional; `Product` has no inverse collection to `OrderItem`.
- **`Payment.order`** owns `Order ↔ Payment`. `@OneToOne` with `@JoinColumn(name = "order_id", nullable = false)` and a unique constraint (`uk_payment_order`) that enforces one-to-one at the DB level. Inverse side `Order.payment` is `@OneToOne(mappedBy = "order", fetch = LAZY)`.
- **`Product.categories`** owns `Product ↔ Category`. `@ManyToMany` with `@JoinTable(name = "product_category", joinColumns = product_id, inverseJoinColumns = category_id)`. Inverse side `Category.products` is `@ManyToMany(mappedBy = "categories")`. Sync helpers `Product.addCategory(Category)` / `Product.removeCategory(Category)` update both sides.
- **`Address`** is `@Embeddable`, not an entity. It has no id, no table, and no owning side — its three fields are columns on `customer`.

---

## 3. Fetch choices

| Association | Fetch | Reason |
|---|---|---|
| `Customer.orders` (`@OneToMany`) | `LAZY` | Collections are unbounded. A customer with thousands of orders must not be pulled in every time the customer is loaded. |
| `Order.items` (`@OneToMany`) | `LAZY` | Same reasoning. `OrderRepository.findByIdWithItems` overrides this per query when the items are needed. |
| `Order.customer` (`@ManyToOne`) | `LAZY` | One cheap FK, but no need to load the customer when only the order is being inspected. |
| `OrderItem.order` (`@ManyToOne`) | `LAZY` | Prevents cycles when `Order.items` is itself being loaded. |
| `OrderItem.product` (`@ManyToOne`) | `LAZY` | The product is a shared entity; no reason to fetch it as a side effect of reading an order line. |
| `Payment.order` (`@OneToOne` owning side) | `LAZY` | Explicit override of the `@OneToOne` default (`EAGER`). The owning side supports lazy because the FK is on `Payment`. |
| `Order.payment` (`@OneToOne` inverse side) | `LAZY` | Marked lazy, but note the JPA limitation: an inverse `@OneToOne` requires bytecode enhancement to actually defer the load. No behavior depends on this laziness. |
| `Product.categories` (`@ManyToMany`) | `LAZY` (default) | M:N collections are lazy unless overridden per query. `ProductRepository.findByCategory` traverses them on demand. |
| `Category.products` (`@ManyToMany` inverse) | `LAZY` (default) | Same. |

**Per-query override:** `OrderRepository.findByIdWithItems` uses `SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id`, which materializes `Order.items` in a single SQL statement despite the `LAZY` mapping. This is a per-query override, not a mapping change. `LEFT` (not inner) so an order with zero items is still returned; `DISTINCT` collapses the duplicated root row that `JOIN FETCH` on a collection produces.

---

## 4. Cascade choices

| Association | Cascade | `orphanRemoval` | Reason |
|---|---|---|---|
| `Order.items` (`@OneToMany`) | `CascadeType.ALL` | `true` | An `OrderItem` has no existence independent of its `Order`. Persisting an order inserts its lines; removing an order deletes its lines; removing a single line from `order.items` deletes that row. |
| `Order.payment` (`@OneToOne` inverse) | none | — | A payment is created explicitly by `OrderService.pay` and persisted via `PaymentRepository`. Cascading `REMOVE` from the order would silently delete the audit-relevant payment row. |
| `Payment.order` (`@OneToOne` owning) | none | — | Same reasoning, opposite direction. `Payment` does not own `Order`'s lifecycle. |
| `Product.categories` (`@ManyToMany`) | `PERSIST` only | — | `PERSIST` so a new `Category` attached to a new `Product` is inserted alongside the product, avoiding `TransientObjectException`. `REMOVE` is deliberately omitted: deleting a product must not delete shared categories. `MERGE` is omitted: `Category` instances are looked up or created inside the service, not merged from detached state. |
| `Category.products` (`@ManyToMany` inverse) | none | — | The inverse side never cascades. |
| `Customer.orders` (`@OneToMany` inverse) | none | — | Orders are created by `OrderService.placeOrder`, not by persisting a customer. Cascading from the customer would create orders as a side effect of registration. |
| `Order.customer` / `OrderItem.order` / `OrderItem.product` (`@ManyToOne`) | none | — | To-one side never cascades. The "many" side does not own the lifecycle of the "one" side. |

**Note on `Product.categories`'s cascade level.** `CascadeType.PERSIST` on a `@ManyToMany` means `em.persist(product)` will cascade to any transient category in the collection. It does **not** cascade `REMOVE` or `MERGE`. `REMOVE` would delete the shared `Category` rows, which is wrong. This is the minimal cascade that makes the association usable, and no more.

---

## 5. Inheritance strategy

**No inheritance is used in this project.** The domain has no is-a hierarchies: `Customer`, `Product`, `Order`, `OrderItem`, `Category`, `Payment`, and `AuditLog` are all independent entities. The brief (§5) lists the entities and does not include any subclass structure.

For completeness: `BaseEntity` is a `@MappedSuperclass`, **not** an inheritance strategy. Its fields (`id`, `createdAt`, `@Version version`) are copied as columns into every entity's table; there is no `base_entity` table and no polymorphic query. This is the standard way to share audit/id fields without introducing a JPA inheritance strategy.

---

## 6. Query decisions

All queries are JPQL or Criteria API, invoked through `em.createQuery(...)`. No Spring Data derived queries, no `JdbcTemplate`, no native Hibernate `Session`.

| Query | Where | Decision |
|---|---|---|
| `CustomerRepository.findByEmail(String)` | Slice 7 | JPQL with a named parameter; returns `Optional<Customer>` via `getResultStream().findFirst()`. The `email` column is unique, so at most one row. Used by `CustomerService.register` for the duplicate-email check. |
| `CustomerRepository.findAll()` | Slice 7 | JPQL with `ORDER BY c.id` for deterministic output. |
| `ProductRepository.findBySku(String)` | Slice 8 | JPQL with a named parameter; `Optional<Product>`. Unique key. |
| `ProductRepository.findByCategory(String)` | Slice 8 | JPQL with an explicit `JOIN p.categories c WHERE c.name = :name`. Compiles to a join on `product_category` and `category`. |
| `ProductRepository.findLowStock(int)` | Slice 8 | JPQL `WHERE p.stock < :threshold ORDER BY p.stock, p.id`. |
| `ProductRepository.findPage(int, int)` | Slice 8 | JPQL with `setFirstResult(page * size)` and `setMaxResults(size)`. These are the JPA `Query` API (not JPQL keywords), because JPQL has no portable `LIMIT`. `ORDER BY p.id` is required for pagination to be stable. |
| `ProductRepository.countAll()` | Slice 8 | JPQL aggregate `SELECT COUNT(p)`. |
| `ProductRepository.search(...)` | Slice 9 | **Criteria API** with a `List<Predicate>`. Each filter (`keyword`, `minPrice`, `maxPrice`, `category`) is added only when its argument is non-null; the join to `categories` is added only when the category filter is requested; the `WHERE` clause is omitted entirely when no filters are supplied. The Criteria API was chosen over JPQL because the alternative would need either multiple query strings or `(:param IS NULL OR ...)` guards, both of which are harder to read and prevent the DB from using indexes on the filtered columns. |
| `OrderRepository.findByIdWithItems(Long)` | Slice 10 | JPQL `SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id`. `JOIN FETCH` avoids the N+1 that would result from `findById` + lazy `items`. `LEFT` so zero-item orders are returned; `DISTINCT` so the root entity is not duplicated by the item join. |
| `OrderRepository.findByCustomer(Long)` | Slice 10 | JPQL `WHERE o.customer.id = :cid ORDER BY o.id`. Uses the FK directly; Hibernate emits `where customer_id = ?`, no join. |
| `OrderRepository.findByStatus(OrderStatus)` | Slice 10 | JPQL with an enum parameter. Because `status` is `@Enumerated(STRING)`, the value is bound as `'NEW'`/`'PAID'`/`'SHIPPED'`/`'CANCELLED'`. |
| `ReportRepository.revenueByCategory()` | Slice 14 | JPQL with a **constructor expression** `new nti.dto.CategoryRevenue(c.name, SUM(oi.unitPrice * oi.quantity))`, restricted to `status IN (PAID, SHIPPED)`. `GROUP BY c.name ORDER BY c.name`. |
| `ReportRepository.topCustomers(int)` | Slice 14 | JPQL constructor expression `new nti.dto.CustomerSpend(o.customer.name, SUM(oi.unitPrice * oi.quantity))`, `GROUP BY o.customer.name ORDER BY SUM(...) DESC`, then `setMaxResults(limit)`. Only PAID/SHIPPED orders. |
| `ReportRepository.ordersPerStatus()` | Slice 14 | Two-column JPQL `SELECT o.status, COUNT(o) GROUP BY o.status`. The `Map<OrderStatus, Long>` is assembled in Java, because the brief specifies a `Map` return type, not a DTO. |
| `ReportRepository.productsNeverOrdered()` | Slice 14 | JPQL `WHERE NOT EXISTS (SELECT 1 FROM OrderItem oi WHERE oi.product = p)`. The brief allows `not exists` or `left join ... is null`; `NOT EXISTS` was chosen for readability. No status filter — the brief says "never ordered" without qualification, so any `OrderItem` row counts. |
| `ReportRepository.monthlySales(int)` | Slice 14 | JPQL constructor expression `new nti.dto.MonthlySales(MONTH(o.orderedAt), SUM(oi.unitPrice * oi.quantity))` with `YEAR(o.orderedAt) = :year`. `MONTH` and `YEAR` are JPA-standard functions, not HQL-only. Only PAID/SHIPPED orders. |
| `ReportRepository.applyDiscount(String, double)` | Slice 15 | **Bulk JPQL update**: `UPDATE Product p SET p.price = p.price * :factor WHERE EXISTS (SELECT 1 FROM p.categories c WHERE c.name = :category)`. This is a DML statement executed via `executeUpdate()`, not a `SELECT`. See §7 below. |

**Cross-cutting decisions:**

- **Collections are lazy by mapping, fetched per query.** `Order.items`, `Customer.orders`, and `Product.categories` are `LAZY`. The one place that needs them eagerly (`findByIdWithItems`) overrides via `JOIN FETCH` rather than changing the mapping.
- **No entity is returned to callers outside a transaction.** `OrderService.getOrderSummary` returns the `OrderSummary` record, not the `Order`, specifically so callers cannot hit a lazy collection after the transaction closes.
- **`equals`/`hashCode` is defined only on `Product`, based on `sku`** (brief §5 requirement). No other entity overrides them. `Set` semantics rely on identity for the other entities, which is sufficient because they are never compared across persistence contexts.

---

## 7. Why `applyDiscount` clears the persistence context

The bulk `UPDATE` in `ReportRepository.applyDiscount` executes a single SQL statement directly against the database. It bypasses the persistence context entirely: it does not load any `Product`, does not trigger dirty checking, and does not touch any `Product` entity that is already managed by the current `EntityManager`.

That bypass is the reason `em.clear()` is required afterwards. The final run of the project shows the problem directly:

```
  In-memory price before applyDiscount = 1500.00
Hibernate: update product ... set price=(p1_0.price*cast(? as numeric(12, 2))) where exists (...)
  After applyDiscount (before re-read)     = 1500.00
Hibernate: select ... from product p1_0 where p1_0.id=?
  After em.clear() + findById              = 1350.00
```

The same `Product` object still holds the pre-update price (`1500.00`) even after the bulk `UPDATE` has run, because the bulk update never informed Hibernate about the change. Without `em.clear()`, a subsequent `em.find(Product.class, id)` on the same `EntityManager` would return that stale object from the first-level cache and never issue a `SELECT`. The database would have `1350.00`, but the application would keep serving `1500.00` for the remainder of the transaction.

`em.clear()` detaches every entity in the persistence context. The next `findById` therefore has nothing cached to return, issues a fresh `SELECT`, and reads the discounted price from the database (`1350.00`). The rule in one line:

> A JPQL bulk `UPDATE` (or `DELETE`) changes the database but does not change the persistence context. Always `clear()` the context afterwards, or refresh the affected entities, if the same transaction will read them again.

The `clear()` is safe in this specific method because `applyDiscount` is the only operation in its transaction: there are no pending changes on other managed entities that clearing would discard. In a larger transaction, `clear()` would also detach unrelated entities and any pending writes to them, so it must be used deliberately.