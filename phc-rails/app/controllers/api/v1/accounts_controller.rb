class Api::V1::AccountsController < ApplicationController
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def show
    account = Account.find_by(sf_id: params[:sf_id])
    if !account.nil?
      render json: account
    else
      api_message_response(400, "Account with that id does not exist.")
    end
  end

  def search
    first_name = request.params[:FirstName]
    last_name = request.params[:LastName]
    cursor = request.params[:cursor]
    result = Account.fuzzy_search({ FirstName: first_name, LastName: last_name }, false)
    result.each do |account|
      if account.sf_id.nil?
        account.sf_id = "None"
      end
    end
    unless cursor.nil?
      result = result[cursor.to_i, 20] # Hardcoded 20 pagination size
    end
    if result.nil?
      respond_with [].to_json
    else
      respond_with result.to_json(only: [:FirstName, :LastName, :Birthdate__c, :sf_id])
    end
  end

  def create
    if not Account.spawn(params).nil?
      api_message_response(200, "Successfully saved account!")
    else
      api_message_response(400, "Account could not be created.")
    end
  end
end
