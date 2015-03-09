class EventRegistrationController < ApplicationController
  before_action :verify_security_token

  def create
    # See if the account exists already

    # If not, create account

    # Else, save to EventRegistration table
  end

  def search
  end

  def update_service
  end

  def update_feedback
  end
end
