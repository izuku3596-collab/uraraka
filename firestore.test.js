const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_deku_123";
const BOB_UID = "bob_bakugo_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProfilePayload(uid, timestampDate) {
  return {
    userId: uid,
    heroName: "Deku (Izuku Midoriya)",
    partnerName: "Uravity (Ochako Uraraka)",
    heroRankTitle: "Héroe en Entrenamiento - Clase 1-A",
    heroLevel: 14,
    currentXp: 2450,
    xpForNextLevel: 3000,
    trainingStreakDays: 8,
    gravityControlMastery: 88,
    combatReflexesMastery: 92,
    vocalPowerMastery: 85,
    enduranceStamina: 90,
    createdAt: timestampDate,
    updatedAt: timestampDate,
  };
}

function validSessionPayload(uid, timestampDate) {
  return {
    userId: uid,
    title: "Control de Gravedad Cero",
    trainingType: "Quirk Sync",
    dateString: "Hoy, 09:30",
    durationMinutes: 45,
    heroPoints: 350,
    rankGrade: "Rango S",
    notes: "Práctica sincronizada con Ochako Uraraka.",
    partnerQuote: "¡Lo lograste, Deku-kun! Tu control mejora cada día.",
    createdAt: timestampDate,
    updatedAt: timestampDate,
  };
}

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

test("Unauthenticated user: cannot read, list, or write profiles or training sessions", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const past = new Date(Date.now() - 2000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID, past))
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("training_sessions").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("training_sessions")
      .doc("sess_1")
      .set(validSessionPayload(ALICE_UID, past))
  );
});

test("Authenticated owner: valid hero profile create & partial XP update succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID, created))
  );

  // Partial update mirroring addHeroXpAndMastery
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      heroLevel: 15,
      currentXp: 150,
      xpForNextLevel: 3500,
      gravityControlMastery: 90,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt modification
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject future timestamp (> request.time)
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      heroLevel: 16,
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  // Reject extra/ghost field on create
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("training_sessions").doc("sess_ghost").set({
      ...validSessionPayload(ALICE_UID, created),
      isAdmin: true,
    })
  );
});

test("Authenticated owner: valid training session create, query, partial update & delete succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  // Create without `@DocumentId` `id` field
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("training_sessions")
      .doc("sess_1")
      .set(validSessionPayload(ALICE_UID, created))
  );

  // Query matching Kotlin HeroTrainingRepository.observeSessions (orderBy createdAt desc)
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("training_sessions")
      .orderBy("createdAt", "desc")
      .get()
  );

  // Reject payload containing `@DocumentId` `id` field inside document data
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("training_sessions")
      .doc("sess_with_id")
      .set({
        id: "sess_with_id",
        ...validSessionPayload(ALICE_UID, created),
      })
  );

  // Reject invalid rankGrade enum
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("training_sessions")
      .doc("sess_bad_rank")
      .set({
        ...validSessionPayload(ALICE_UID, created),
        rankGrade: "Rango SSS+",
      })
  );

  // Reject oversized notes (> 500 chars)
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("training_sessions")
      .doc("sess_long_notes")
      .set({
        ...validSessionPayload(ALICE_UID, created),
        notes: "A".repeat(501),
      })
  );

  // Partial update on notes succeeds
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("training_sessions")
      .doc("sess_1")
      .update({
        notes: "Actualizado con éxito",
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  // Delete succeeds for owner
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("training_sessions")
      .doc("sess_1")
      .delete()
  );
});

test("Cross-user isolation: Alice cannot read, list, create, update, or delete Bob's data", async () => {
  const past = new Date(Date.now() - 10000);
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await db.collection("users").doc(BOB_UID).set(validProfilePayload(BOB_UID, past));
    await db
      .collection("users")
      .doc(BOB_UID)
      .collection("training_sessions")
      .doc("bob_sess_1")
      .set(validSessionPayload(BOB_UID, past));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Top-level list on users must fail
  await assertFails(aliceDb.collection("users").get());
  // Cross-user profile read/write must fail
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).update({ heroLevel: 99 }));
  // Cross-user subcollection read/list/write/delete must fail
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("training_sessions").get()
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("training_sessions")
      .doc("bob_sess_1")
      .get()
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("training_sessions")
      .doc("bob_sess_2")
      .set(validSessionPayload(ALICE_UID, past))
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("training_sessions")
      .doc("bob_sess_1")
      .delete()
  );
});
