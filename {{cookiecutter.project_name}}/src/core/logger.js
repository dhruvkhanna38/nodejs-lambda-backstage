const bunyan = require('bunyan')
const { config } = require('./config')
const { logLevel, appName } = config

const log = bunyan.createLogger({
  name: appName,
  streams: [
    {
      stream: process.stdout,
      level: logLevel
    }
  ],
  serializers: bunyan.stdSerializers
})

module.exports = { log }
