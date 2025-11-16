variable "aws_region" {
  description = "La région AWS"
  type        = string
  default     = "us-east-1"  # Région mise à jour
}

variable "cluster_name" {
  description = "Nom du cluster EKS"
  type        = string
  default     = "mykubernetes"  # Nom du cluster mis à jour
}

variable "subnet_ids" {
  type    = list(string)
  default = []
}


variable "role_arn" {
  description = "ARN du rôle IAM pour EKS"
  type        = string
  default     = "arn:aws:iam::775955735712:role/LabRole"  # Valeur par défaut
}


variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"  # Modifiez-le selon vos besoins
}



