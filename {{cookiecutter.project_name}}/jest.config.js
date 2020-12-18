const jestConfig = {
  collectCoverage: true,
  coverageReporters: [
    'lcov',
    'text'
  ],
  collectCoverageFrom: [
    'src/**/*.js'
  ],
  coverageThreshold: {
    global: {
      branches: 88,
      functions: 98,
      lines: 97,
      statements: 97
    }
  }
}

module.exports = jestConfig
