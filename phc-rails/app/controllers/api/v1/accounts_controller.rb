class Api::V1::AccountsController < ApplicationController
  respond_to :json
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def show
    sf_id = params[:sf_id]
    respond_with Account.find_by sf_id: sf_id
  end

  def search
    first_name = request.params[:FirstName]
    last_name = request.params[:LastName]
    cursor = request.params[:cursor]
    result = Account.fuzzy_search({ FirstName: first_name, LastName: last_name }, false)
    for account in result
      if account.sf_id.nil?
        account.sf_id = "None"
      end
    end
    unless cursor.nil?
      result = result[cursor.to_i, 20] # Hardcoded 20 pagination size
    end
    respond_with result.to_json(only: [:FirstName, :LastName, :Birthdate__c, :sf_id])
  end

  def create
    if not Account.spawn(params).nil?
      respond_with "Successfully saved account!", status: 200, location: root_url
    else
      respond_with "Error saving account!", status: 200, location: root_url
    end
  end
end
