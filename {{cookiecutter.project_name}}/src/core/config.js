const config = {
  aws: { region: process.env.REGION || 'ap-southeast-2' },
  logLevel: process.env.LOG_LEVEL || 'info',
  appName: process.env.APP_NAME || 'please-add-app-name-in-env'
}

module.exports = { config }
