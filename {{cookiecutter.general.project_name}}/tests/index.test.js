const { handler } = require('./index')
const events = require('events')
const eventEmitter = new events.EventEmitter()
const myEventHandler = function () {
  console.log('I hear a scream!')
}
eventEmitter.on('scream', myEventHandler)

describe('Gets Data back from handler function', () => {
  it('execute the handler function', () => {
    expect(handler(eventEmitter.emit('scream'), { awsRequestId: 'foo-id' })).not.toEqual(undefined)
  })
})
