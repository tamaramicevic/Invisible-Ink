const config = {};

// moved these values to .env
// config.host = process.env.HOST || "https://invisble-inc-cosmos-db-dev.documents.azure.com:443/";
// config.authKey =
//   process.env.AUTH_KEY || "hpgFDCaWyKfx6Wmr8ZfDNjVrH4b95OuMy8tfLdiqWjmP1zjDWHSuS0BEE5pZ9yP0tiLiC2DJL8eyrQBhD6M79Q==";
config.databaseId = "InvisibleInkDb";
config.containerId = "InvisibleInkContainer";

if (config.host.includes("https://localhost:")) {
  console.log("Local environment detected");
  console.log("WARNING: Disabled checking of self-signed certs. Do not have this code in production.");
  process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
  console.log(`Go to http://localhost:${process.env.PORT || '3000'} to try the sample.`);
}

module.exports = config;