class SessionsController < ApplicationController
  respond_to :json
  skip_before_action :verify_authenticity_token

  def new
  end

  def login
    user = User.find_by(email: params[:email].downcase)
    if user && user.authenticate(params[:password])
      user.remember
      data = {user_id: user.id, auth_token: user.auth_token}
      respond_with data, json
    else
      respond_with "Error: User authentication failed.", status: 401
    end
  end

  def destroy
    user = User.find_by(email: params[:email].downcase)
    auth_token = params[:auth_token]
    if user && user.authenticated?(auth_token)
      user.end_session
      respond_with "Successfully logged out.", status: 200
    else
      respond_with "Error: Failed to logout.", status: 401
    end
  end

  def current_user(user_id, auth_token)
    user = User.find_by(id: user_id)
    if user && user.authenticated?(auth_token)
      @current_user = user
    end
  end

end
