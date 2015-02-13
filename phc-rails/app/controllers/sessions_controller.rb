class SessionsController < ApplicationController
  skip_before_action :verify_authenticity_token

  def new
  end

  def login
    user = User.find_by(email: params[:email].downcase)
    if user && user.authenticate(params[:password])
      # Remember user and return the auth token
      user.remember
      data = {user_id: user.id, auth_token: user.auth_token}
      puts user.auth_digest
      render :json => data
    else
      # Return error message
      data = {message: "Error: User authentication failed."}
      render :json => data
    end
  end

  def current_user(user_id, auth_token)
    user = User.find_by(id: user_id)
    if user && user.authenticated?(auth_token)
      @current_user = user
    end
  end

end
