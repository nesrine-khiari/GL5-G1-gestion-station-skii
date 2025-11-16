# ------------------------------
# Région AWS
# ------------------------------
variable "aws_region" {
  description = "La région AWS où se trouve le cluster et le VPC"
  type        = string
  default     = "us-east-1"
}

# ------------------------------
# Nom du cluster
# ------------------------------
variable "cluster_name" {
  description = "Nom du cluster EKS"
  type        = string
  default     = "mykubernetes"
}

# ------------------------------
# CIDR du VPC
# ------------------------------
variable "vpc_cidr" {
  description = "CIDR block du VPC"
  type        = string
  default     = "10.0.0.0/16"
}
