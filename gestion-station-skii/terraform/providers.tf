provider "aws" {
  region = var.aws_region

  assume_role {
    role_arn = var.role_arn
  }
}
