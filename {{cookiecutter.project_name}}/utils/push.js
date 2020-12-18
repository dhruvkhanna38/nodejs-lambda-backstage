const execSync = require('child_process').execSync
const { prompt } = require('inquirer')

async function idValidator(input){
  if(input.length===0){
    throw new Error("ID cannot be Empty");
  }
  if(input.length!==7){
    throw new Error("Invalid ID");
  }
  if(input[0]=='d' || input[0]=='D' || input[0]==='h' || input[0]==='H'){
    return true;
  }else{
    throw new Error("ID must begin with the letter d/D or h/H");
  }
}

prompt([
  {
    type: 'input',
    message: 'Enter Your Telstra DID',
    name: '_id',
    validate: async (input)=>{
      return await idValidator(input);
    }
  },
  {
    type: 'input',
    message: 'Enter JIRA Issue ID',
    name: 'issueId',
    validate: async (input)=>{
      var jiraMatcher = /((?!([A-Z0-9a-z]{1,10})-?$)[A-Z]{1}[A-Z0-9]+-\d+)/g;
      if(jiraMatcher.test(input)){
        return true;
      }else{
        throw new Error("Invalid Jira Issue ID");
      }
    }
  }
]).then(async (answers) => {
  try {
    execSync('git add .')
    execSync(`git commit -m "build(automatic commit): ${answers._id}, ${answers.issueId} automatic commit using upload script ${Math.random().toString(36).substring(7)} "`)
    execSync('git push -u origin master')
  } catch (error) {
    console.log(error)
  }
})
