// Database Connection to Mongodb for reference

// const mongoose = require("mongoose");

// const serverURI = process.env.DATABASE_URL || "mongodb://127.0.0.1:27017/book";

// class DBConnection {
//   constructor() {
//     this._connect();
//   }
//   _connect() {
//     mongoose
//       .connect(serverURI, { useNewUrlParser: true, useUnifiedTopology: true })
//       .then(() => {
//         console.log("Database connection successful");
//       })
//       .catch(err => {
//         console.error("Database connection error");
//         console.log(err);
//       });
//   }
// }

// module.exports = new DBConnection();
