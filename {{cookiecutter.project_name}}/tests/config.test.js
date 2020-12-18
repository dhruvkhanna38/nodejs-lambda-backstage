const { config } = require('./config')
describe('Config Object has valid keys and values', () => {
  it('is not undefined', () => {
    expect(config).not.toEqual(undefined)
  })

  describe('Has valid values for logLevel and appName', () => {
    expect(config.logLevel).toEqual('info')
    expect(config.appName).toEqual('please-add-app-name-in-env')
  })
})
