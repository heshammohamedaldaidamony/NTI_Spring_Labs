# Library Management System — README


---

## Relationships & owning sides

| Association | Kind | Owning side | Stored as |
|---|---|---|---|
| `Author` ↔ `Book` | `@OneToMany` / `@ManyToOne` (bidirectional) | **`Book`** — `@ManyToOne @JoinColumn(name="author_id")` | FK `author_id` on `Book` |
| `Book` → `Publisher` | `@ManyToOne` (unidirectional) | **`Book`** — the only side | FK `publisher_id` on `Book` |
| `Book` ↔ `Category` | `@ManyToMany` (bidirectional) | **`Book`** — `@JoinTable(name="book_category")` | join table `book_category(book_id, category_id)` |
| `Person` → `Employee` / `Customer` | inheritance, not an association | — | one `Person` table + `person_type` discriminator |

Inverse sides use `mappedBy`: `Author.books` (`mappedBy="author"`), `Category.books` (`mappedBy="categories"`). Sync helpers `Author.addBook()` and `Book.addCategory()` keep both sides consistent in memory.

---

## Fetch choices

| Association | Fetch | Why |
|---|---|---|
| `Author.books`, `Category.books`, `Book.categories` | `LAZY` (default) | Collections grow unboundedly; loading them eagerly is rarely wanted. Slice 7 showed the resulting `LazyInitializationException`; slice 11 fixed it per query. |
| `Book.author` | `EAGER` (default, left implicit) | One cheap FK; a book without its author is not a valid state. |
| `Book.publisher` | `EAGER` (written explicitly as `fetch = FetchType.EAGER`) | Same reasoning as `Book.author`. Written out because the brief asked for the to-one choice to be explicit. |

`JOIN FETCH a.books` (slice 11) is a **per-query override** of the `LAZY` mapping, not a change to the mapping itself.

---

## Cascade choices

| Association | Cascade | orphanRemoval | Why |
|---|---|---|---|
| `Author.books` | `CascadeType.ALL` | `true` | Book is a child of Author in this model. Persisting an Author inserts its books (slice 3, Scenario A); removing a Book from the list `DELETE`s its row (Scenario B). |
| `Book.categories` | none | — (not allowed on `@ManyToMany`) | Categories are shared; cascading `REMOVE` would delete Category rows themselves. |
| `Book.author`, `Book.publisher` | none | — | The "many" side does not own the "one" side's lifecycle. |

---

## Inheritance strategy

`Employee` and `Customer` extend `Person`. Strategy: **`SINGLE_TABLE`** with `@DiscriminatorColumn(name="person_type")` and `@DiscriminatorValue("EMP")` / `("CUST")` (slice 6).

**Why:** the hierarchy is tiny and the subclasses share almost all their columns. `SINGLE_TABLE` needs no joins and no polymorphic union — one row per person, one table. The cost (nullable subclass-only columns like `department`, `email`) is irrelevant at this size.

Alternatives: `JOINED` would split into three tables and require joins on every polymorphic load; `TABLE_PER_CLASS` would require `UNION ALL` for polymorphic queries.

---

## Query decisions

| Query | Where | Decision |
|---|---|---|
| Books by author name | Slice 8 | JPQL, **named** parameter. `b.author.name` is a path expression; Hibernate adds the join implicitly. |
| Books by publisher (A): `b.publisher.name = :name` | Slice 9 | JPQL, named parameter. Emits `join Publisher`; use when you only have the name. |
| Books by publisher (B): `b.publisher = :publisher` | Slice 9 | JPQL, entity parameter. Compares FK directly (`where publisher_id = ?`); no join. Use when you already hold the `Publisher`. |
| Book by id: `WHERE b.id = ?1` | Slice 10 | JPQL, **positional** parameter. Included because the brief required it; `em.find` is the idiomatic PK lookup. |
| Author with books: `JOIN FETCH a.books` | Slice 11 | One `SELECT` instead of N+1; also prevents `LazyInitializationException` after `em.close()`. Per-query override, not a mapping change. |
| Author book counts: `SELECT a.name, COUNT(b) ... LEFT JOIN ... GROUP BY a.name` | Slice 12 | `LEFT JOIN` (not `JOIN`) so zero-book authors appear with `0`. `COUNT(b)` compiles to `count(b.id)`. Projection is `Object[]`. |
| Criteria: book by title | Slice 13 | Single predicate; compiles to the same SQL as JPQL. Introduces `CriteriaBuilder` / `CriteriaQuery` / `Root`. |
| Criteria: dynamic optional filters (title + author) | Slice 14 | `List<Predicate>` + `cb.and(...)`; predicates and join added only when their input is non-null; `where` skipped entirely when no filters given. The case where Criteria beats JPQL (no `IS NULL OR` guards, no multiple query strings). |
| JPQL vs HQL comparison | Slice 15 | Executed query is standard JPQL (portable); HQL's `FETCH ALL PROPERTIES` described only, to keep the project provider-agnostic. |

**Cross-cutting rule:** collections stay `LAZY` in the mapping; queries that need them use `JOIN FETCH` to fetch in one statement.