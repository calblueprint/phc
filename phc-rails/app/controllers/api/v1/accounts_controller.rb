class Api::V1::AccountsController < ApplicationController
  respond_to :json
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def show
    sf_id = params[:sf_id]
    respond_with Account.find_by sf_id: sf_id
  end

  def search
    first_name = request.headers["HTTP_FIRSTNAME"]
    last_name = request.headers["HTTP_LASTNAME"]
    result = Account.fuzzy_search({ FirstName: first_name, LastName: last_name }, false)
    respond_with result.to_json(only: [:FirstName, :LastName, :Birthdate__c, :sf_id])
  end

  def create
    Account.create(params)
  end
end
