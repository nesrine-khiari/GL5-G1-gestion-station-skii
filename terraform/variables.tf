variable "aws_region" {
  description = "La région AWS"
  type        = string
  default     = "us-east-1"
}

variable "cluster_name" {
  description = "Nom du cluster EKS"
  type        = string
  default     = "mykubernetes"
}

variable "role_arn" {
  description = "ARN du rôle IAM pour EKS (LabRole)"
  type        = string
  default     = "arn:aws:iam::744983671605:role/LabRole"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}
