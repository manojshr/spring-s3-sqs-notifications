provider "aws" {
  region                      = "us-east-1"
  access_key                  = "test"
  secret_key                  = "test"

  # only required for non virtual hosted-style endpoint use case.
  # https://registry.terraform.io/providers/hashicorp/aws/latest/docs#s3_force_path_style
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true
  s3_force_path_style         = true

  endpoints {
    s3  = "http://localstack:4566"
    sqs = "http://localstack:4566"
  }

  default_tags {
    tags = {
      Environment = "Local"
      Service     = "LocalStack"
    }
  }
}

terraform {
  required_version = "= 1.11.4"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 3.60.0, <= 3.69.0"
    }
  }
}
