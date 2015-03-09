class EventRegistrationController < ApplicationController
  before_action :verify_security_token

  def create
    # Create account if does not exist
    if not Account.exists?(sf_id: params[:account_sfid])
      params[:sf_id] = params.delete :account_sfid # Change key to sf_id
      account = Account.spawn(params)
    else
      account = Account.find_by(sf_id: params[:account_sfid])

    # Save to EventRegistration table

  end

  def search
  end

  def update_service
  end

  def update_feedback
  end
end
