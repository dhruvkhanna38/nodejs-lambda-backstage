const jestConfig = {
    moduleFileExtensions: [
      "js",
      "jsx",
      "json",
      "ts",
      "tsx"
    ],
    moduleNameMapper: {
      "^@/(.*)$": "<rootDir>/src/$1"
    },
    testMatch: [
      "**/*.test.js"
    ],
    coverageThreshold: {
      global: {
        branches: 75,
        functions: 75,
        lines: 75,
        statements: 75
      }
    }
  }
  
  module.exports = jestConfig