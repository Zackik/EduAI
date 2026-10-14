const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// 1. Unauthenticated checks
test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Unauthenticated user: cannot read study groups", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("study_groups").get());
});

// 2. Authenticated checks & Cross-user Isolation
test("Authenticated user: Alice can create and read her own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileRef = aliceDb.collection("users").doc(ALICE_UID);
  
  await assertSucceeds(
    profileRef.set({
      userId: ALICE_UID,
      displayName: "Alice",
      email: "alice@example.com",
      major: "Computer Science",
      yearOfStudy: 3,
      totalXp: 120,
      streakDays: 4,
      createdAt: new Date(),
    })
  );

  await assertSucceeds(profileRef.get());
});

test("Authenticated user: Bob cannot read Alice's profile", async () => {
  // Seed Alice's profile using admin context
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      email: "alice@example.com",
      major: "Computer Science",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: Alice can create study plans under her path", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const planRef = aliceDb.collection("users").doc(ALICE_UID).collection("plans").doc("plan_1");

  await assertSucceeds(
    planRef.set({
      planId: "plan_1",
      userId: ALICE_UID,
      title: "Data Structures & Algorithms",
      subject: "DSA",
      dailyMinutes: 45,
      progressPercent: 20,
      status: "active",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: Bob cannot read Alice's study plans", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("plans").doc("plan_1").set({
      planId: "plan_1",
      userId: ALICE_UID,
      title: "Secret Plan",
      subject: "DSA",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("plans").doc("plan_1").get());
});

test("Leaderboard: Any authenticated user can read leaderboard and write own entry", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set({
      entryId: ALICE_UID,
      userId: ALICE_UID,
      displayName: "Alice",
      major: "Computer Science",
      totalXp: 500,
      streakDays: 5,
      updatedAt: new Date(),
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(bobDb.collection("leaderboard").doc(ALICE_UID).get());
});
