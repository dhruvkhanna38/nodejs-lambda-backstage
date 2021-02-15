# NodeJS - Example Microservice Project Structure

## To run project execute below two commands
- docker-compose build
- docker-compose up


#### Clean and Delete node_nodules
```
npm run clean
```

### Lint and Fix
```
npm run lint
```

### Run Unit Tests
```
npm run test:unit
```

### Test and Generate Coverage
```
npm run test
```

### Commit
```
npm run commit
```

#### Note
```
Please set environment variable for this microservice while deployment. The types of Environment variables are given below.
["DEV", "TEST", "QA", "UAT", "SVT", "PREPROD", "PRODUCTION"]. By default the environment is set to 'DEV' in clf-node for logging.

eg ENVIRONMENT = DEV

Follow the format as given inside 'dev.env' and 'test.env' files inside config folder. 
```