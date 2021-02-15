const express = require("express");
const compression = require("compression");
const app = express();
const BodyParser = require("body-parser");
const clfNode = require("@telstra/clf-node");
const fs = require("fs")

const file_stream = fs.createWriteStream("./app_logs", { flags: "a" });
clfNode.attach([file_stream], process.stdout);
clfNode.attach([file_stream], process.stderr);

const BookRoutes = require("../routes/BookRoutes");

// parse application/json
app.use(BodyParser.json());
app.use(compression());
app.use(
  clfNode.middlewares.express({
    capture_request_body: true /* combine_http_events: true */,
  }),
);

app.use("/api/book", BookRoutes);

module.exports = app;
