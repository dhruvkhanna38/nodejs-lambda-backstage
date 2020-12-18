const axios = require('axios')
const { prompt } = require('inquirer')
const fs = require('fs')
const path = require('path')
const execSync = require('child_process').execSync
const slugify = require('slugify')


const baseURL = 'https://git02.ae.sda.corp.telstra.com/rest/api/1.0'

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

async function checkIfProjectKeyExists(projectKey,_id, password){
          const keyData = await axios.get(`${baseURL}/projects/${projectKey}`, { auth: { username: _id, password: password }});
          if(keyData.status === 200){
              return true;
          }else{
              throw new Error("Invalid Key or Resource Not Accessible.");
          }
}

async function getReposForTeam (projectKey, _id, password) {
  try {
    let size = 0
    let start = 0
    const repos = []
    do {
      const teamData = await axios.get(`${baseURL}/projects/${projectKey}/repos?limit=100&start=${start}`, { auth: { username: _id, password: password } })
      size = teamData.data.size
      teamData.data.values.forEach(value => {
        repos.push(value.slug)
      })
      start = start + 100
    } while (size !== 0)
    return repos
  } catch (error) {
    throw new Error(error)
  }
}

async function createRepo (projectKey, _id, password, repoName) {
  try {
    const json = JSON.stringify({ name: repoName, scmId: 'git', forkable: true })
    await axios.post(`${baseURL}/projects/${projectKey}/repos`,
      json,
      { auth: { username: _id, password: password }, headers: { 'Content-Type': 'application/json' } })
  } catch (error) {
    throw new Error(error)
  }
}


let repoNameFromBambooSpecs = 'nodejs-lambda-repository';

              (async ()=>{

                const _id = await prompt([
                    {
                        type: 'input',
                        message: 'Enter your Telstra DID',
                        name: '_id',
                        validate: async (input)=>{
                            return await idValidator(input);
                          }
                    }
                ])
                const password = await prompt([
                    {
                        type: 'password',
                        message: 'Enter you Account-01 Password',
                        name: 'password',
                        validate: async (input)=>{
                            if(input.length === 0){
                                throw new Error("Password Cannot be Empty");
                            }
                            const verifyCredentials = await axios.get(`${baseURL}/users?limit=1`, { auth: { username: _id._id, password: input }})
                            if(verifyCredentials.status !== 200){
                                throw new Error("Invalid Credentials. Please check your ID and Password");
                            }
                            return true;
                        }
                    }
                ])
                const projectKey = await prompt([
                    {
                        type: 'input',
                        message: 'Enter The Bitbucket Project Key Where you want to create the repository',
                        name: 'projectKey',
                        default:'SRED',
                        validate: async (input)=>{
                            if(input.length === 0){
                                throw new Error("Project Key Cannot be Empty");
                            }
                            return await checkIfProjectKeyExists(input, _id._id, password.password);
                        }
                    }
                ])
                const repoName = await prompt([
                    {
                        type:'input',
                        name:'repoName',
                        message:'Enter Repository Name',
                        default:repoNameFromBambooSpecs,
                        validate: async (input)=>{
                                if(input.length === 0){
                                    throw new Error("Repository Name Cannot be Empty");
                                }
                                if(input.length<7){
                                    throw new Error("Repository Name must be atleast 7 characters long");
                                }
                                const repositories = await getReposForTeam(projectKey.projectKey,_id._id, password.password);
                                if (repositories.indexOf(slugify(input)) !== -1) {
                                    throw new Error('This Repository name is not available. Please use a different repository name.')
                                }
                                return true;
                            }
                    }
                ])
                return {..._id, ...password, ...projectKey, ...repoName}
              })().then(async (answers)=>{
                    try {
                        console.log(`✅ Wait while we create a repo for you`);
                        await createRepo(answers.projectKey, answers._id, answers.password, answers.repoName);
                        execSync('git add .')
                        execSync(`git commit -m "Initial Commit by ${answers._id}" --no-verify`)
                        execSync(`git remote add origin https://${answers._id}@git02.ae.sda.corp.telstra.com/scm/${answers.projectKey}/${slugify(answers.repoName)}.git`)
                        execSync('git push -u origin master')
                        console.log(`✅ Your Template has been generated and has been uploaded to bitbucket`);
                    } catch (error) {
                        console.log(error)
                    }
              })