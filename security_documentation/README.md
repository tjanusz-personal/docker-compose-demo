# AWS Security Documentation

## AWS Inspector findings
W/in my AWS account I have enabled AWS inspector to automatically scan all ECR based images push to my ECR repos.

On push of container image it should start an auto scan.. First glance seems like we need to ensure we have containers that are built according to AWS 'standards'.
For example, if I tried to just use a simple 'tomcat:9' container AWS scan results returned 'no findings' but using 'tomcat:9-jdk17-corretto' resulted in actual scan findings.

Open items to consider:
* can use ECR repo filters (need to flup on this)
* will generate cloud watch events (need flup on this)
 

General Notes in inspector2
* uses open source clair 
* supports lots of OS (https://docs.aws.amazon.com/inspector/latest/user/supported.html)
* Inspector 2 will scan container images on initial upload and ongoing CVE DB update (all scans) 
 ** ISSUE: When continuous scanning is enabled for a repository, if an image hasn't been updated in the past 30 days based on the image push timestamp, then continuous scanning is suspended for that image

ECR Scanning types:
* BASIC: on upload or ondemand 1x a day, last results only,
* ENHANCED: on upload, continuous w/CVE DB updated, sends results to security hub, scans for both OS and package level
 
Integrates into security hub
* shows all findings from inspector into console
* uses the ASFF (AWS security hub findings format)

Event Bridge Events
* scan complete w/findings
* change in finding state
* newly aggregated findings

Costs:
* .09 per image upload (ECR)
* .01 per image on rescan (ECR)
* $1.25 avg # of per instance (EC2)
 
Questions for AWS:
* 30 day limit on continuous scanning when image not updated? Can it be changed?
* Inspector scanning is JavaScript scanning same as NodeJS scanning?
* Does BASIC scanning include both OS level and package level scanning like enhanced does?
* Can we schedule enhanced scans? Doesn't seem like we can? Not sure what our workaround is?
  


## AWS cli commands
```
# see finding for specific image tag..
aws ecr describe-image-scan-findings --repository-name docker-compose-demo-tomcat --image-id imageDigest=sha256:get_sha_from_image
```

These are some various [aws cli commands to interact with inspector2](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/inspector2/index.html)

```
# show all covered resouce ids
aws inspector2 list-coverage | jq '.coveredResources[].resourceId'

# select out a subset of findings into csv format
aws inspector2 list-findings | jq '.findings[] | [.awsAccountId, .inspectorScore, .severity, .type, .title] | @csv'

# pull out all resources with id and type
aws inspector2 list-findings | jq '.findings[].resources[] | [.id, .type] | @csv'

# pull out key elements to see list of main issues
aws inspector2 list-findings | jq '.findings[] | {accountId:.awsAccountId, type:.type, severity:.severity, title:.title, score:.inspectorScore, res_type:.resources[].type, res_id:.resources[].id }'

# alpha sort findings by severity
aws inspector2 list-findings | jq '.findings | sort_by(.severity)[] | {accountId:.awsAccountId, type:.type, severity:.severity, title:.title, score:.inspectorScore, res_type:.resources[].type, res_id:.resources[].id }'

# alpha sort findings by severity and return as csv file
aws inspector2 list-findings | jq '.findings | sort_by(.severity)[] | [.awsAccountId, .type, .severity, .title, .inspectorScore, .resources[].type, .resources[].id ]  | @csv'

```

Using these screen shots you can see the results appear in my AWS account under inspector

![Sample Inspector findings][sample_inspector_image]

[sample_inspector_image]: ./AWS_inspector_results.png "Sample AWS Inspector findings"


Using these screen shots you can see the results appear in my AWS account under security hub

![Sample Security Hub findings][sample_security_hub_image]

[sample_security_hub_image]: ./AWS_security_hub_results.png "Sample AWS Security Hub findings"


