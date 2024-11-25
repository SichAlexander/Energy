const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.addDevice = functions.https.onRequest(async (req, res) => {
  const { uuid, region, model } = req.body;

  if (!uuid || !region || !model) {
    return res.status(400).json({ error: "Missing required fields: uuid, region, model" });
  }

  try {
    const newDevice = {
      uuid,
      region,
      model,
      lastUpdate: new Date().toISOString(),
    };

    const devicesRef = admin.database().ref("devices");
    await devicesRef.push(newDevice);

    return res.status(200).json({ message: "Device added successfully!" });
  } catch (error) {
    console.error("Error adding device:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
});
