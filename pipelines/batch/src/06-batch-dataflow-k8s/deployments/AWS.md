### S3

- Create the S3 bucker `dataflow-bucket-lab`
- Create the User with the following policy `dataflow-bucket-lab-policy`

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
        {
        "Effect": "Allow",
        "Action": ["s3:ListBucket"],
        "Resource": ["arn:aws:s3:::dataflow-bucket-lab"]
        },
        {
        "Effect": "Allow",
        "Action": [
            "s3:PutObject",
            "s3:GetObject",
            "s3:DeleteObject"
        ],
        "Resource": ["arn:aws:s3:::dataflow-bucket-lab/*"]
        }
    ]
    }
    ```

- Create an user attached with previous policy `DataflowUserLab`

- Check the credentials using `awscli`

        aws sts get-caller-identity

    ``` json
    {
        "Account": "399872922500",
        "UserId": "AIDAV2GSISOCM44RY3RFP",
        "Arn": "arn:aws:iam::399872922500:user/DataflowUserLab"
    ```

- List the objects from bucket (via api or command)

        aws s3api list-objects --bucket dataflow-bucket-lab --query 'Contents[].{Key: Key, Size: Size}'

        aws s3api list-objects --bucket eks-lab-dev-bucket --query 'Contents[].{Key: Key, Size: Size}'

    ``` json
    [
        {
            "Size": 173098,
            "Key": "sample-data-prod.zip"
        }
    ]
    ```

        aws s3 ls s3://dataflow-bucket-lab

        2019-04-08 14:37:33     173098 sample-data-prod.zip
        
- Get objects using API

        aws s3api get-object --bucket dataflow-bucket-lab --key sample-data-prod.zip sample-data-prod.zip

        aws s3api get-object --bucket eks-lab-dev-bucket --key sample-data-prod.zip sample-data-prod.zip

- List the objects via minio

    #Install minio or use docker instead minio/mc
    wget https://dl.minio.io/client/mc/release/linux-amd64/mc
    chmod +x mc
    sudo mv mc /usr/bin/
    
    ./mc --help
    
    # Doc https://docs.min.io/docs/minio-client-complete-guide
    # mc config host add <ALIAS> <YOUR-S3-ENDPOINT> <YOUR-ACCESS-KEY> <YOUR-SECRET-KEY> <API-SIGNATURE>
    
    mc config host add s3 https://s3.amazonaws.com $AWS_ACCESS_KEY_ID $AWS_SECRET_ACCESS_KEY  --api S3v4
    # This is the same but using different region
    mc config host add s3 https://s3-eu-west-2.amazonaws.com $AWS_ACCESS_KEY_ID $AWS_SECRET_ACCESS_KEY  --api S3v4
    
    mc ls s3/dataflow-bucket-lab
    