class SessionsController < ApplicationController
  skip_before_action :verify_authenticity_token

  def new
  end

  def create
    user = User.find_by(email: params[:email].downcase)
    if user && user.authenticate(params[:password])
      # Log in user
      log_in user
      render nothing: true
    else
      # Return error message
      return "Error: User authentication failed."
    end
  end

end
