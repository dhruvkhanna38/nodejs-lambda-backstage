const { log } = require('./logger')

describe('A log is created using bunyan', () => {
  it('is not undefined', () => {
    expect(log).not.toEqual(undefined)
  })
})
