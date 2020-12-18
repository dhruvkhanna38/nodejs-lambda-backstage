const { log } = require('../../core/logger')

const handler = async (event, context) => {
  const requestId = context.awsRequestId

  log.info({ requestId, event }, 'Incoming  event')

  return event
}

module.exports = { handler }
