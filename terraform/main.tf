data "aws_region" "current" {}

resource "aws_sqs_queue" "user_todo_queue" {
  name = "user-todo-s3-event-notification-queue"
}

resource "aws_sqs_queue" "user_profile_queue" {
  name = "user-profile-s3-event-notification-queue"
}

resource "aws_s3_bucket" "user_bucket" {
  bucket = "user-bucket"
}

resource "aws_s3_bucket_notification" "user-bucket-notification" {
  bucket = aws_s3_bucket.user_bucket.id

  queue {
    id = "user-profile-notifications"
    queue_arn = aws_sqs_queue.user_profile_queue.arn
    events = ["s3:ObjectCreated:*"]
    filter_prefix = "user-profile/"
  }

  queue {
    id = "user-todo-notifications"
    queue_arn = aws_sqs_queue.user_todo_queue.arn
    events = ["s3:ObjectCreated:*"]
    filter_prefix = "user-todo/"
  }
}
