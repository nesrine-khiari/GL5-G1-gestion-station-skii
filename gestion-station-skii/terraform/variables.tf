# ------------------------------
# AWS Region
# ------------------------------
variable "aws_region" {
  description = "The AWS region where the cluster and VPC are located"
  type        = string
  default     = "us-east-1"
}

# ------------------------------
# Cluster Name
# ------------------------------
variable "cluster_name" {
  description = "Name of the EKS cluster"
  type        = string
  default     = "mykubernetes"
}

# ------------------------------
# VPC CIDR
# ------------------------------
variable "vpc_cidr" {
  description = "CIDR block of the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

# ------------------------------
# Node Group Configuration
# ------------------------------
variable "capacity_type" {
  description = "Type of capacity for the node group (ON_DEMAND or SPOT)"
  type        = string
  default     = "ON_DEMAND"
}

variable "instance_types" {
  description = "List of instance types for the node group"
  type        = list(string)
  default     = ["t3.medium"]
}

variable "desired_size" {
  description = "Desired number of worker nodes"
  type        = number
  default     = 2
}

variable "max_size" {
  description = "Maximum number of worker nodes"
  type        = number
  default     = 3
}

variable "min_size" {
  description = "Minimum number of worker nodes"
  type        = number
  default     = 1
}
