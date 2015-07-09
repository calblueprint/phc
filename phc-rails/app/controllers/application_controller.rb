class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception
  respond_to :json

  include SessionsHelper

  def user_authenticated?(id, token)
    user = User.find(id)
    if user.nil?
      byebug
      api_message_response(401, "User with that id does not exist.")
      return false
    elsif not user.authenticated?(token)
      byebug
      api_message_response(401, "Invalid authentication token.")
      return false
    else
      return true
    end
  end

  def verify_security_token
    auth_token = request.headers["HTTP_AUTH_TOKEN"]
    user_id = request.headers["HTTP_USER_ID"]
    if auth_token && user_id
      return true if user_authenticated?(user_id, auth_token)
    else
      api_message_response(401, "Please include authentication token.")
    end
    return false
  end

  def api_message_response(status, message="")
    render json: { message: message }, status: status
  end

end
