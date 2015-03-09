class Api::V1::EventRegistrationsController < ApplicationController
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def create
    byebug
    reg = EventRegistration.new

    # Create account if salesforce id was not passed in
    sf_id = params[:account_sfid]
    if sf_id.nil?
      params[:sf_id] = params.delete :account_sfid # Change key to sf_id
      account = Account.spawn(params)
      reg[:account_sfid] = account.id
    else
      if not Account.exists?(sf_id: sf_id)
        raise "Unknown Salesforce ID"
      end
      account = Account.find_by(sf_id: params[:account_sfid])
      reg[:account_sfid] = sf_id
    end

    reg[:Number__c] == params[:Number__c]

    respond_with "Successfully registered user!", status: 200, location: root_url
  end

  def search
  end

  def update_service
  end

  def update_feedback
  end

end
