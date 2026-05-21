# AGENT.md — Pharmacovigilance Android App

> **Read this file first.** It contains the full project context, current state, architecture decisions, and what needs to be built next. If anything below conflicts with a user request, ask before deviating.

---

## 1. Project overview

A **client-facing prototype** of a pharmacovigilance mobile app — a digital platform for patients and healthcare professionals to declare adverse drug reactions (effets indésirables) and track the status of their declarations.

The app is inspired by the Marbio pharmacovigilance reporting forms (https://marbio.com/prep/pharmacovigilance/) and the broader Wellys pharmacovigilance app concept. The end goal is a connected platform that transmits declarations to a central pharmacovigilance system; this prototype fakes that backend entirely with local storage so we can demo the user-facing flow to the client.

### Target users
- **Patients** — declare adverse reactions they've experienced.
- **Healthcare professionals** — doctors, pharmacists, nurses — declaring on behalf of patients.

### Demo goal (CURRENT MILESTONE)
A runnable Android app that can be shown to the client on a phone or emulator, demonstrating:
1. Login as a demo user (patient or healthcare pro).
2. A dashboard listing previously submitted cases with colored status pills.
3. A multi-step declaration form that captures all required pharmacovigilance fields.
4. A case detail screen showing the full declaration + a status timeline.
5. Status workflow simulated locally: a newly submitted case animates through `RECEIVED → UNDER_ANALYSIS → VALIDATED` over ~15 seconds so the demo feels alive.

**There is no real backend, no real network, no real authentication.** Everything runs offline against a local Room database. This is deliberate — the client wants to see the UX, not the infrastructure.

---

## 2. Tech stack and constraints

| Concern | Choice | Why |
|---|---|---|
| Language | **Java** | Client/team preference. Do NOT introduce Kotlin files. |
| Activity template | Empty Views Activity (XML layouts) | Compose is Kotlin-only. |
| UI | XML + Material Components + View Binding | Standard, stable, easy to demo. |
| Architecture | MVVM with `ViewModel` + `LiveData` | First-class Java support, survives rotation. |
| Local storage | **Room 2.8.x** (NOT Room 3.x — that's Kotlin-only) | Demo data lives on the device. |
| Navigation | Navigation Component (XML nav graph) v2.9.x | NOT Navigation3 — that's Compose-only. |
| Lists | RecyclerView with ListAdapter + DiffUtil | Smooth list updates when status flips. |
| Images | Glide | Java-friendly (Coil is Kotlin-only). |
| Async | `ExecutorService` + Room `LiveData` | Room's LiveData removes most threading concerns. |
| Min SDK | 26 (Android 8.0) | Covers ~95% of devices, modern APIs. |
| Languages displayed | French (primary), Arabic + English as stretch | Marbio is a Moroccan company. |

### Dependencies are declared in a Gradle version catalog
`gradle/libs.versions.toml` already contains everything needed. Do NOT add new third-party libraries without checking with the user first. Use `annotationProcessor(libs.room.compiler)` for Room — never `implementation`.

### Things to deliberately SKIP for the demo
These are listed because they're tempting to build but are explicitly out of scope right now:
- Real authentication, signup flow, password recovery.
- Network/REST calls, Retrofit, OkHttp.
- Push notifications (FCM).
- Barcode scanning (ML Kit) and camera capture (CameraX) — dependencies exist in the catalog but don't wire them.
- PDF receipt generation.
- Real localization beyond French strings.
- Tests beyond the auto-generated ones.

If a feature would require any of the above, fake it locally or stub the UI with a "Coming soon" toast.

---

## 3. Architecture

Three-layer MVVM, organized by feature:

```
Presentation (Activities, Fragments, ViewModels, XML layouts, adapters)
        ↓ observes LiveData
Domain     (the entities ARE the domain models — no separate use-case layer for this demo)
        ↓
Data       (Repositories → Room DAOs → SQLite)
```

### Decisions already made (do not relitigate)
- **Enums are stored as Strings** in Room (`severity`, `status`, `user_type`). No `TypeConverter`. The entity exposes typed getters like `getStatusEnum()`.
- **No Hilt, no Koin.** The `Application` subclass `PharmacovigilanceApp` is the singleton container; repositories are accessed via `PharmacovigilanceApp.get().getCaseRepository()`. Simple, demo-appropriate.
- **The fake status workflow lives in `CaseRepository.submitCase()`** — see section 5. Don't move it elsewhere.
- **Single-Activity, multi-Fragment.** `LoginActivity` is the only secondary Activity (it has no bottom nav and no toolbar). After login, everything happens inside `MainActivity`'s `NavHost`.

---

## 4. Current file inventory

### ✅ Already implemented (in the canvas / chat history)

```
app/src/main/java/com/yourname/pharmacovigilance/
├── PharmacovigilanceApp.java                       ✅
├── data/
│   ├── local/
│   │   ├── AppDatabase.java                        ✅
│   │   ├── CaseDao.java                            ✅
│   │   ├── UserDao.java                            ✅
│   │   └── entity/
│   │       ├── CaseEntity.java                     ✅
│   │       ├── CaseStatus.java                     ✅
│   │       ├── Severity.java                       ✅
│   │       ├── UserEntity.java                     ✅
│   │       └── UserType.java                       ✅
│   └── repository/
│       ├── CaseRepository.java                     ✅
│       └── UserRepository.java                     ✅
└── util/
    └── SeedData.java                               ✅
```

These files exist in the codebase. Verify them by reading; only modify if there's a real bug.

### ✅ UI layer — completed in initial scaffold

```
app/src/main/java/com/yourname/pharmacovigilance/
├── MainActivity.java                               ✅ NavHost + BottomNav
├── ui/
│   ├── auth/
│   │   ├── LoginActivity.java                      ✅
│   │   └── LoginViewModel.java                     ✅
│   ├── dashboard/
│   │   ├── DashboardFragment.java                  ✅
│   │   ├── DashboardViewModel.java                 ✅
│   │   └── CaseAdapter.java                        ✅ ListAdapter + DiffUtil
│   ├── declaration/
│   │   ├── DeclarationViewModel.java               ✅ nav-graph-scoped
│   │   ├── Step1PatientFragment.java               ✅ patient initials, sex, age
│   │   ├── Step2ProductFragment.java               ✅ product name, scan stub
│   │   ├── Step3EventFragment.java                 ✅ description, severity, date picker
│   │   └── Step4ReviewFragment.java                ✅ review + submit
│   ├── casedetail/
│   │   ├── CaseDetailFragment.java                 ✅ with timeline
│   │   └── CaseDetailViewModel.java                ✅
│   ├── profile/
│   │   └── ProfileFragment.java                    ✅
│   └── common/
│       ├── StatusPillView.java                     ✅ MaterialTextView subclass
│       └── ViewModelFactory.java                   ✅
└── util/
    ├── SessionManager.java                         ✅
    └── DateFormatter.java                          ✅ French relative dates
```

NOTE: `DeclarationHostFragment` was dropped from the design — the 4 step fragments
navigate to each other directly via the nested `declaration_graph`, and the shared
ViewModel is obtained by scoping to `R.id.declaration_graph`. No host wrapper needed.

### XML resources to create

```
app/src/main/res/
├── layout/
│   ├── activity_login.xml                          ❌ P0
│   ├── activity_main.xml                           ❌ P0  (NavHost + BottomNavigationView)
│   ├── fragment_dashboard.xml                      ❌ P0
│   ├── item_case.xml                               ❌ P0  (one card in the RecyclerView)
│   ├── fragment_declaration_host.xml               ❌ P1
│   ├── fragment_step1_patient.xml                  ❌ P1
│   ├── fragment_step2_product.xml                  ❌ P1
│   ├── fragment_step3_event.xml                    ❌ P1
│   ├── fragment_step4_review.xml                   ❌ P1
│   └── fragment_case_detail.xml                    ❌ P2
├── navigation/
│   └── main_nav_graph.xml                          ❌ P0
├── menu/
│   └── bottom_nav_menu.xml                         ❌ P0
├── values/
│   ├── strings.xml                                 ⚠️  needs French strings added
│   ├── colors.xml                                  ⚠️  needs status colors added
│   └── themes.xml                                  ⚠️  Material3 theme (default is fine)
├── values-fr/
│   └── strings.xml                                 ❌ P1  (French translations)
└── drawable/
    ├── bg_status_pill.xml                          ❌ P0  (rounded background for the pill)
    ├── ic_logo.xml                                 ❌ P0  (placeholder logo — simple vector)
    └── (Material icons for bottom nav)             ❌ P0
```

### Manifest

`AndroidManifest.xml` must declare:
- `android:name=".PharmacovigilanceApp"` on `<application>`.
- `LoginActivity` as the LAUNCHER activity (replace `MainActivity` as launcher).
- `MainActivity` as a regular activity.
- No internet permission needed (we have no network).

---

## 5. Key behavior specs

### Login flow
- `LoginActivity` opens first. Two `TextInputEditText` fields (email, password) + a "Connexion" button.
- No signup screen — pre-seeded demo accounts only. Show a small text block under the form: `Démo: patient@demo.com / demo  •  doc@demo.com / demo`.
- On success: save the user id via `SessionManager.setUserId(id)`, `startActivity(MainActivity)`, `finish()`.
- On failure: Snackbar "Identifiants incorrects".

### Dashboard
- Header: "Bonjour, {user.fullName}".
- A `RecyclerView` of `CaseEntity`s, observed from `caseRepository.observeCasesForUser(userId)`.
- Each row: product name (bold), submission date (relative — "Il y a 2 jours"), and a colored `StatusPillView`.
- A FAB (Floating Action Button) bottom-right with a `+` icon → navigates to the declaration wizard.
- Empty state: a centered illustration + "Aucune déclaration pour le moment. Touchez + pour commencer."
- Tap on a row → `CaseDetailFragment`.

### Status pill colors
| Status | Background | Text |
|---|---|---|
| RECEIVED | gray 100 | gray 800 |
| UNDER_ANALYSIS | amber 100 | amber 800 |
| INFO_REQUESTED | blue 100 | blue 800 |
| VALIDATED | green 100 | green 800 |
| CLOSED | gray 200 | gray 700 |

Use Material Design tonal colors. Add concrete hex values in `colors.xml` — don't depend on dynamic theming.

### Declaration wizard (4 steps)
A single `DeclarationViewModel` holds the in-progress `CaseEntity` and is **scoped to the nav graph**, not to the fragment, so step 1's data survives navigating to step 2.

In Java with Navigation 2.x, this is done via:
```java
NavController nc = NavHostFragment.findNavController(this);
NavBackStackEntry entry = nc.getBackStackEntry(R.id.declaration_graph);
ViewModelProvider provider = new ViewModelProvider(entry, factory);
DeclarationViewModel vm = provider.get(DeclarationViewModel.class);
```

#### Step 1 — Patient info
Fields: patient initials (text), sex (radio M/F), age (number). A checkbox "Je suis le patient" — when checked, the initials field auto-fills from the logged-in user's name.

#### Step 2 — Product
Fields: product name (text). A "Scanner le code-barres" button → for the demo, just show a toast "Fonctionnalité disponible bientôt" (the dependency is in the catalog but we're not wiring it).

#### Step 3 — Event description
Fields: event description (multiline text), severity (4 radio buttons: Mineur / Sérieux / Mettant la vie en danger / Fatal), date d'apparition (date picker → epoch millis).

#### Step 4 — Review and submit
Read-only summary of everything entered. "Modifier" button per section → `popBackStack` to that step. "Envoyer" button → calls `caseRepository.submitCase(case, callback)`, on `onDone` it shows a toast "Déclaration envoyée" and `popBackStack` to the dashboard.

### The fake workflow (already implemented in `CaseRepository`)
Don't add anything to this — just make sure the Dashboard correctly re-renders when statuses flip. Because the DAO returns `LiveData<List<CaseEntity>>`, the RecyclerView updates automatically; the `CaseAdapter` should use `ListAdapter<CaseEntity, VH>` with a `DiffUtil.ItemCallback` so individual rows animate smoothly instead of the whole list flashing.

### Case detail
Read-only view of all fields, plus a vertical status timeline showing:
- ● Reçu — {submittedAt}
- ● En cours d'analyse — {when it changed} (grayed if not reached)
- ○ Validé (grayed if not reached)

For the demo, only the `RECEIVED` timestamp is real; the others can use `submittedAt + 5s` / `+15s` as approximations.

---

## 6. Build / run instructions

```bash
# From the project root
./gradlew assembleDebug
# Or just hit ▶ in Android Studio with a device/emulator attached.
```

Min Android version: 8.0 (API 26). The app should work on the default Android Studio emulator (Pixel 6, API 34 image).

### How to verify the demo works
1. Launch app → `LoginActivity` appears with the demo credentials hint visible.
2. Log in as `patient@demo.com` / `demo`.
3. Dashboard shows 2 seeded cases (one VALIDATED with green pill, one UNDER_ANALYSIS with amber pill).
4. Tap the FAB → wizard appears at step 1.
5. Fill all 4 steps → submit.
6. Return to dashboard. The new case shows as RECEIVED (gray).
7. Wait 5 seconds — pill flips to amber (UNDER_ANALYSIS).
8. Wait 10 more seconds — pill flips to green (VALIDATED).
9. Tap any case → detail screen with the full info and status timeline.

If any of those nine steps don't work, the demo is broken.

---

## 7. Coding conventions

- **Package root: `com.yourname.pharmacovigilance`** — but the user might use their own root. Search for `com.yourname.pharmacovigilance` and adapt all files consistently. Never leave a mismatched package.
- **One public class per file.** Standard Java.
- **View Binding always.** Never `findViewById`. The generated class is named like `ActivityLoginBinding`.
- **String resources.** Every user-facing string in `strings.xml` (or `values-fr/strings.xml`). No hardcoded strings in layouts or code.
- **Resource naming:** `activity_*`, `fragment_*`, `item_*`, `dialog_*`, `bg_*`, `ic_*`. Snake_case.
- **Java naming:** PascalCase classes, camelCase methods/fields, ALL_CAPS constants.
- **Null safety:** `@NonNull` / `@Nullable` on every public API parameter and return type.
- **No anonymous Threads, no AsyncTask, no raw Handler in UI code.** Background work goes through the repository's `ExecutorService`. The only `Handler` is the one in `CaseRepository` for the fake workflow delays.
- **Don't suppress warnings without a comment explaining why.**

---

## 8. Common pitfalls (read before debugging)

- **Room compilation errors** → 99% of the time, `annotationProcessor(libs.room.compiler)` is missing from `build.gradle`, or the entity has a constructor Room can't pick. Fix: add `@Ignore` to convenience constructors, leave a no-arg one public.
- **`@NonNull` field crashes at insert** → a `@NonNull` field was left null when building the entity. Check the constructor.
- **LiveData fires once then nothing** → you observed with `this` (a Fragment) instead of `getViewLifecycleOwner()`. Always use `getViewLifecycleOwner()` from Fragments.
- **Navigation argument missing** → forgot to add `<argument>` in the nav graph XML. Use `Bundle` or Safe Args.
- **Bottom nav highlight desyncs after deep navigation** → use `NavigationUI.setupWithNavController(bottomNav, navController)`. Don't manually `setSelectedItemId`.
- **Fragments share state when they shouldn't** → you used `new ViewModelProvider(requireActivity())` instead of `requireParentFragment()` or the nav-graph-scoped one. Pick the right scope.
- **Status pill doesn't update** → the adapter is comparing references in DiffUtil. Make sure `areContentsTheSame` compares `status` (and any field that changes), not just `id`.

---

## 9. Current state and what's left

**Runnable demo: COMPLETE.** All P0/P1/P2 features from the original plan are built. The 9-step demo check at section 6 should pass end-to-end.

### Verification checklist for Claude Code

Before declaring the project ready to demo, run through these:

1. `./gradlew assembleDebug` succeeds with no errors.
2. The launched APK shows `LoginActivity` first.
3. Logging in with `patient@demo.com` / `demo` opens the dashboard with 2 seeded cases.
4. The seeded `Paracétamol` card shows a green `Validé` pill; the `Amoxicilline` card shows an amber `En cours d'analyse` pill.
5. FAB opens the 4-step wizard. Each step's `Précédent` returns to the previous step and preserves entered data.
6. Submitting at step 4 returns to dashboard. The new card initially shows `Reçu` (gray), flips to amber at +5s and green at +15s.
7. Tapping any card opens `CaseDetailFragment`, with full info and a status timeline.
8. Profile tab shows the logged-in user's name and a working logout button.
9. After logout, the app returns to `LoginActivity` and `SessionManager` no longer reports logged-in.

### Optional next iterations (NOT in scope for v1)

- Wire ML Kit + CameraX to make the barcode scanner real (deps are already in `libs.versions.toml`).
- Add photo attachments (Glide is already on the classpath).
- Add a "force status" debug menu (long-press the dashboard greeting) — `CaseRepository.forceStatus()` already exists.
- Real signup flow.
- A connectivity stub showing how the demo would call a real API once a backend exists.

---

## 10. When in doubt

- If a requirement here conflicts with what the user just asked for, **the user wins** — but ask them whether to update this file.
- If a library version in `libs.versions.toml` produces deprecation warnings, leave it alone unless the build breaks.
- If you find yourself wanting to add a backend, a network library, a DI framework, or Kotlin — stop and ask first.
- If a file from section 4 already exists but looks different from what this doc implies, **read it before overwriting**. The existing code is canonical; this doc may be slightly stale.
