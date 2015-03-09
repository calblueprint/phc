class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception
  include SessionsHelper

  def user_authenticated?(id, token)
    user = User.find_by(id: id)
    if not user
      respond_with "Error: User ID not found in database.", status: 401
      false
    elsif not user.authenticated?(token)
      respond_with "Error: Invalid authentication token.", status: 401
      false
    else
      true
    end
  end

  def verify_security_token
    auth_token = request.headers["HTTP_AUTH_TOKEN"]
    user_id = request.headers["HTTP_USER_ID"]
    if auth_token && user_id
      if user_authenticated?(user_id, auth_token)
        return true
      end
    else
      respond_with "Error: Please include authentication token.", status: 401
    end
  end

end
