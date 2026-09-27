# Employee Management System — Spring Core Mini Project

A plain Spring (non-Boot) application demonstrating IoC, DI, bean configuration,
scopes, lifecycle, profiles, and externalized configuration. Built with Maven,
Java 21, Spring 6.2.

## Bean scopes — which is what, and why

| Bean | Scope | Why |
|---|---|---|
| `InMemoryEmployeeRepository` / `FileBackedEmployeeRepository` | singleton | Stateless-ish storage the whole app shares. One instance is correct. |
| `EmployeeServiceImpl` | singleton | Stateless service. Must be one instance so the injected `ObjectProvider<AuditLogger>` can hand out fresh loggers per call. |
| `EmployeeValidator` | singleton | Pure logic, no state. |
| `EmailNotifier`, `SmsNotifier`, `PushNotifier`, `PriorityEmailNotifier` | singleton | Stateless message emitters. |
| `NotificationManager` | singleton | Holds the injected `List<Notifier>`; must be one instance so all callers share the same list. |
| `CompanyInfo`, `AliasProbe`, `LegacyBean`, `FileStoreInitializer`, `ImportedLifecycleMarker` | singleton (default) | Plain configuration / lifecycle beans. |
| `AuditLogger` | **prototype** | Must be a fresh instance every time it's used, so each log line carries its own timestamp + instance id. A singleton would leak state across calls and defeat the "one log per usage" intent. |
| `LazyBean` | singleton, `@Lazy` | Delays construction until first use; still one instance once created (Day 3 §8.3 — @Lazy is "when", not "how many"). |

## Injection types — used where, and why

| Injection point | Type | Why |
|---|---|---|
| `EmployeeServiceImpl` (constructor) | constructor | Required dependencies (`EmployeeRepository`, `NotificationManager`, `EmployeeValidator`, `ObjectProvider<AuditLogger>`). Day 3 §7: constructor is the default for required deps — final fields, no half-built objects. |
| `NotificationManager.setNotifiers(List<Notifier>)` | setter | Collection injection (Day 3 §7: setter is appropriate for collection/optional-style deps). |
| `NotificationManager.setPreferredSingleNotifier(Notifier)` | setter + `@Qualifier` | Deliberate second single-injection point to demonstrate `@Qualifier` overriding `@Primary` (Day 3 §6.4). |
| `CompanyInfo` setter fields | setter + `@Value` | Externalized configuration (Day 3 §6.5 / §7 — "optional / configuration" case). |
| `AliasProbe.employeeRepository` (XML `autowire="byType"`) | setter, XML autowire | Lab B6 — compares XML-side autowire with Java `@Autowired`. |
| `EmployeeServiceImpl.maxRaisePercentage` | field + `@Value` | Deliberate third injection style so the README can note that field injection works but is discouraged (Day 3 §7). |

## The scoped-bean problem and how it was solved

`EmployeeServiceImpl` is a **singleton** and needs a **fresh `AuditLogger`** (a prototype bean) on every call. Naive injection (`private final AuditLogger logger;`) would give the service one prototype instance for the lifetime of the app — the exact trap Day 3 §8.3 warns about.

We solved it by injecting **`ObjectProvider<AuditLogger>`** into the constructor and calling `.getObject()` at the top of every public method. `ObjectProvider` is Spring's recommended modern replacement for `@Lookup` (Day 3 §11.2). Additionally, the `AuditLogger` class carries `@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)`, so direct field injection *would also work* if we ever wanted it — the scoped proxy resolves a fresh target on every method call. We chose `ObjectProvider` because it makes the "I want a fresh one here" intent explicit at the call site.

For completeness, `nti.demos.DemosRunner` also demonstrates the older `@Lookup` alternative — same problem, solved a different way — so both tutorial-covered approaches appear in the project.

## dev vs prod profile

`nti.repository.EmployeeRepository` has two implementations:

- `InMemoryEmployeeRepository` — annotated `@Profile("dev")`, discovered by `@ComponentScan`.
- `FileBackedEmployeeRepository` — declared in `applicationContext.xml` inside a `<beans profile="prod">` block, built via an XML `factory-method` (`FileBackedFactory.create("employees.csv")`) with `depends-on="fileStoreInitializer"` for ordering.

`Main` sets the active profile programmatically (`context.getEnvironment().setActiveProfiles("dev")` before `refresh()`). Switching to `"prod"` swaps the wired repository with no Java changes — the whole point of `@Profile` (Day 3 §10.1).

## Required features checklist

| Assignment requirement | Where |
|---|---|
| `Employee` with id/name/department/salary | `nti.model.Employee` |
| `EmployeeRepository` (save/findById/findAll) + in-memory impl | `nti.repository.EmployeeRepository`, `InMemoryEmployeeRepository` |
| `EmployeeService` + impl; deps injected, no `new` | `nti.service.EmployeeServiceImpl` |
| `addEmployee`, `getEmployeeById`, `getAllEmployees`, business logic (`giveRaise`) | `nti.service.EmployeeService` |
| `@Configuration` class with `@Bean` | `nti.config.AppConfig` |
| `@ComponentScan` + `@Repository` + `@Service` | `AppConfig`, `InMemoryEmployeeRepository`, `EmployeeServiceImpl` |
| Two repo impls + `@Profile` dev/prod, active profile set from `Main` | `InMemoryEmployeeRepository`, `FileBackedEmployeeRepository`, `AppConfig`, `Main`, `applicationContext.xml` |
| `Notifier` + 3 impls; `List<Notifier>` injection; `@Order` | `nti.notify.*` |
| `NotificationManager` used from the service (add + raise events) | `EmployeeServiceImpl.addEmployee` / `.giveRaise` |
| `EmployeeValidator` + custom exception; called before save/raise | `nti.service.EmployeeValidator`, `InvalidEmployeeException` |
| Prototype `AuditLogger`; proof of new instance; scoped-bean problem solved | `nti.audit.AuditLogger`, `EmployeeServiceImpl`, `DemosRunner` |
| `@PostConstruct` / `@PreDestroy`; context closed from `Main` | `EmployeeServiceImpl`, `Main` |
| `application.properties` with 4 values + `@PropertySource` + `@Value` | `src/main/resources/application.properties`, `AppConfig`, `CompanyInfo`, `EmployeeServiceImpl` |
| `Main` demonstrates the whole flow in order | `nti.Main` |
| Console output captured | this README (below) |

## Final console output

```
========================================================
  0. Spring context starting — lifecycle callbacks below
========================================================
[CompanyInfo] InitializingBean.afterPropertiesSet()
[FileStoreInitializer] preparing file store (runs first)
[EmployeeServiceImpl] @PostConstruct: dependencies are wired, bean is ready
[ImportedLifecycleMarker] constructor
[ImportedLifecycleMarker] @Bean initMethod: start()
[LegacyBean] constructor
[LegacyBean] defaultInit()
[LegacyBean] constructor
[LegacyBean] defaultInit()

========================================================
  1. Add a valid employee (validation runs, then all 3 notifiers fire)
========================================================
[AuditLogger#1147805316] constructed at 23:20:52.721541100
[AuditLogger#1147805316] addEmployee(Alice)
[PriorityEmailNotifier] New employee added: Alice
[EmailNotifier] New employee added: Alice
[SmsNotifier] New employee added: Alice
[PushNotifier] New employee added: Alice
[AuditLogger#403174823] constructed at 23:20:52.724543800
[AuditLogger#403174823] getAllEmployees()
Employees now: [Employee{id=1, name='Alice', department='Engineering', salary=8000.0}]

========================================================
  2. Add an invalid employee — blank name (validation failure handled)
========================================================
[AuditLogger#1764996806] constructed at 23:20:52.726543
[AuditLogger#1764996806] addEmployee(   )
Rejected as expected: Employee name must not be blank

========================================================
  3. Add an invalid employee — negative salary (validation failure handled)
========================================================
[AuditLogger#1651162064] constructed at 23:20:52.727543700
[AuditLogger#1651162064] addEmployee(Bob)
Rejected as expected: Employee salary must not be negative (was -1.0)

========================================================
  4. Give a raise within the allowed limit (10% <= 15%)
========================================================
[AuditLogger#983595261] constructed at 23:20:52.728544
[AuditLogger#983595261] giveRaise(1, 10.0)
[PriorityEmailNotifier] Raise for Alice: 8000.0 -> 8800.0
[EmailNotifier] Raise for Alice: 8000.0 -> 8800.0
[SmsNotifier] Raise for Alice: 8000.0 -> 8800.0
[PushNotifier] Raise for Alice: 8000.0 -> 8800.0

========================================================
  5. Give a raise exceeding the allowed limit (30% > 15%)
========================================================
[AuditLogger#940857381] constructed at 23:20:52.734780600
[AuditLogger#940857381] giveRaise(1, 30.0)
Rejected as expected: Raise 30.0% exceeds allowed maximum of 15%

========================================================
  6. Prototype AuditLogger — two fetches must be different instances
========================================================
[AuditLogger#508512860] constructed at 23:20:52.741779800
[AuditLogger#925973605] constructed at 23:20:52.742858900
a1 identityHashCode = 508512860
a2 identityHashCode = 925973605
a1 == a2 ? false   (expected: false)

========================================================
  7. List all employees
========================================================
[AuditLogger#2038522556] constructed at 23:20:52.743858100
[AuditLogger#2038522556] getAllEmployees()
Employee{id=1, name='Alice', department='Engineering', salary=8800.0}

========================================================
  8. Externalized @Value properties (from application.properties)
========================================================
CompanyInfo{name='NTI Training', currency='EGP', retryCount=3, maxRaisePercentage=15}

========================================================
  9. Active profile + chosen repository implementation
========================================================
Active profiles : [dev]
Repository bean : InMemoryEmployeeRepository

========================================================
  10. Closing context — destroy callbacks fire below
========================================================
[LegacyBean] defaultDestroy()
[LegacyBean] defaultDestroy()
[ImportedLifecycleMarker] @Bean destroyMethod: stop()
[EmployeeServiceImpl] @PreDestroy: context is shutting down
[CompanyInfo] DisposableBean.destroy()
```
