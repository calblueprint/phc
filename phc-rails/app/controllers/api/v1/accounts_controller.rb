class Api::V1::AccountsController < ApplicationController
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def show
    account = Account.find_by(sf_id: params[:sf_id])
    if account.nil?
      render status: 400,
        json: { message: "Account with that id does not exist." } and return
    end
    render json: account.attributes.slice(*Account::API_FIELDS.map(&:to_s))
  end

  def search
    first_name, last_name, cursor = request.params.values_at(:FirstName, :LastName, :cursor)
    result = Account.fuzzy_search({ FirstName: first_name, LastName: last_name }, false)
    result.each do |account|
      account.sf_id ||= "None"
    end

    unless cursor.blank?
      result = result[cursor.to_i, 20] # Hardcoded 20 pagination size
    end
    if result.nil? || result.empty?
      render json: []
    else
      render json: result.to_json(only: [:FirstName, :LastName, :Birthdate__c, :sf_id])
    end
  end

  def create
    account = Account.spawn(params)
    if not account.nil?
      account.update(modified: true)
      api_message_response(200, "Successfully saved account!")
    else
      api_message_response(400, "Account could not be created.")
    end
  end

end
